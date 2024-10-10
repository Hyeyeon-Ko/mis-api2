package kr.or.kmi.mis.api.confirm.service.Impl;

import kr.or.kmi.mis.api.apply.model.request.ConfirmRequestDTO;
import kr.or.kmi.mis.api.authority.model.entity.Authority;
import kr.or.kmi.mis.api.authority.repository.AuthorityRepository;
import kr.or.kmi.mis.api.bcd.model.entity.BcdDetail;
import kr.or.kmi.mis.api.bcd.model.entity.BcdMaster;
import kr.or.kmi.mis.api.bcd.model.response.BcdDetailResponseDTO;
import kr.or.kmi.mis.api.bcd.repository.BcdDetailRepository;
import kr.or.kmi.mis.api.bcd.repository.BcdMasterRepository;
import kr.or.kmi.mis.api.bcd.repository.impl.BcdApplyQueryRepositoryImpl;
import kr.or.kmi.mis.api.confirm.model.request.BcdApproveRequestDTO;
import kr.or.kmi.mis.api.confirm.model.request.BcdDisapproveRequestDTO;
import kr.or.kmi.mis.api.confirm.model.response.BcdHistoryResponseDTO;
import kr.or.kmi.mis.api.confirm.service.BcdConfirmService;
import kr.or.kmi.mis.api.doc.model.entity.DocMaster;
import kr.or.kmi.mis.api.doc.repository.DocMasterRepository;
import kr.or.kmi.mis.api.exception.EntityNotFoundException;
import kr.or.kmi.mis.api.noti.service.NotificationSendService;
import kr.or.kmi.mis.api.std.model.entity.StdDetail;
import kr.or.kmi.mis.api.std.model.entity.StdGroup;
import kr.or.kmi.mis.api.std.repository.StdDetailRepository;
import kr.or.kmi.mis.api.std.repository.StdGroupRepository;
import kr.or.kmi.mis.api.std.service.StdBcdService;
import kr.or.kmi.mis.api.user.service.InfoService;
import kr.or.kmi.mis.cmm.model.request.PostSearchRequestDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class BcdConfirmServiceImpl implements BcdConfirmService {

    private final BcdMasterRepository bcdMasterRepository;
    private final BcdDetailRepository bcdDetailRepository;
    private final NotificationSendService notificationSendService;
    private final StdBcdService stdBcdService;
    private final InfoService infoService;
    private final AuthorityRepository authorityRepository;
    private final StdGroupRepository stdGroupRepository;
    private final StdDetailRepository stdDetailRepository;
    private final DocMasterRepository docMasterRepository;
    private final BcdApplyQueryRepositoryImpl bcdApplyQueryRepositoryImpl;

    @Override
    @Transactional(readOnly = true)
    public BcdDetailResponseDTO getBcdDetailInfo(String draftId) {
        BcdMaster bcdMaster = bcdMasterRepository.findById(draftId)
                .orElseThrow(() -> new EntityNotFoundException("BcdMaster not found"));
        BcdDetail bcdDetail = bcdDetailRepository.findById(draftId)
                .orElseThrow(() -> new EntityNotFoundException("BcdDetail not found for draft ID: " + draftId));

        String drafter = bcdMaster.getDrafter();
        // 기준자료에서 각 기준자료 코드에 해당하는 명칭 불러오기
        List<String> names = stdBcdService.getBcdStdNames(bcdDetail);

        return BcdDetailResponseDTO.of(bcdDetail, drafter, names);
    }

    @Override
    @Transactional
    public void approve(String draftId, ConfirmRequestDTO confirmRequestDTO) {

        // 1. 승인
        BcdMaster bcdMaster = bcdMasterRepository.findById(draftId)
                .orElseThrow(() -> new EntityNotFoundException("BcdMaster not found for draft ID: " + draftId));

        if (!bcdMaster.getCurrentApproverId().equals(confirmRequestDTO.getUserId())) {
            throw new IllegalArgumentException("현재 결재자가 아닙니다.");
        }

        boolean isLastApprover = bcdMaster.getCurrentApproverIndex() == bcdMaster.getApproverChain().split(", ").length - 1;

        String approverId = confirmRequestDTO.getUserId();
        String approver = infoService.getUserInfoDetail(approverId).getUserName();

        BcdApproveRequestDTO approveRequest = BcdApproveRequestDTO.builder()
                .approverId(approverId)
                .approver(approver)
                .respondDate(LocalDateTime.now())
                .status(isLastApprover ? "B" : "A")  // 마지막 결재자라면 B(완료), 아니면 A(승인 대기)
                .build();

        bcdMaster.updateCurrentApproverIndex(bcdMaster.getCurrentApproverIndex() + 1);
        bcdMaster.updateApprove(approveRequest);

        bcdMasterRepository.save(bcdMaster);

        // 2. 팀장, 파트장, 본부장 -> ADMIN 권한 및 사이드바 권한 취소 여부 결정
        StdGroup stdGroup1 = stdGroupRepository.findByGroupCd("C002")
                .orElseThrow(() -> new IllegalArgumentException("Not Found"));

        String instCd = infoService.getUserInfoDetail(approverId).getInstCd();
        List<StdDetail> stdDetail1 = stdDetailRepository.findByGroupCdAndEtcItem1(stdGroup1, instCd)
                .orElseThrow(() -> new IllegalArgumentException("Not Found"));

        boolean userIdExistsInStdDetail = stdDetail1.stream()
                .anyMatch(detail -> approverId.equals(detail.getEtcItem2()) || approverId.equals(detail.getEtcItem3()));

        if (userIdExistsInStdDetail) {
            return;
        }

        // 권한 취소 여부 결정
        List<BcdMaster> bcdMasterList = bcdMasterRepository.findAllByStatusAndCurrentApproverIndex("A", 0)
                .orElseThrow(() -> new IllegalArgumentException("Not Found"));
        List<DocMaster> docMasterList = docMasterRepository.findAllByStatusAndCurrentApproverIndex("A", 0)
                .orElseThrow(() -> new IllegalArgumentException("Not Found"));

        boolean shouldCancelAdminByBcd = true;
        boolean shouldCancelAdminByDoc = true;

        for (BcdMaster master : bcdMasterList) {
            if (master.getCurrentApproverId().equals(approverId)) {
                shouldCancelAdminByBcd = false;
                break;
            }
        }

        for (DocMaster master : docMasterList) {
            if (master.getCurrentApproverId().equals(approverId)) {
                shouldCancelAdminByDoc = false;
                break;
            }
        }

        if (shouldCancelAdminByBcd && shouldCancelAdminByDoc) {
            Authority authority = authorityRepository.findByUserIdAndDeletedtIsNull(approverId)
                    .orElseThrow(() -> new IllegalArgumentException("Not Found"));

            // ADMIN 권한 취소
            authority.deleteAdmin(LocalDateTime.now());
            authorityRepository.save(authority);

            // 사이드바 권한 취소
            StdGroup stdGroup2 = stdGroupRepository.findByGroupCd("B002")
                    .orElseThrow(() -> new IllegalArgumentException("Not Found"));
            StdDetail stdDetail2 = stdDetailRepository.findByGroupCdAndDetailCd(stdGroup2, approverId)
                    .orElseThrow(() -> new IllegalArgumentException("Not Found"));

            stdDetailRepository.delete(stdDetail2);
        }
    }

    /* 반려 */
    @Override
    @Transactional
    public void disapprove(String draftId, ConfirmRequestDTO confirmRequestDTO) {
        BcdMaster bcdMaster = bcdMasterRepository.findById(draftId)
                .orElseThrow(() -> new EntityNotFoundException("BcdMaster not found for draft ID: " + draftId));
        BcdDetail bcdDetail = bcdDetailRepository.findById(draftId)
                .orElseThrow(() -> new EntityNotFoundException("BcdDetail not found for draft ID: " + draftId));

        String disapproverId = confirmRequestDTO.getUserId();
        String disapprover = infoService.getUserInfoDetail(disapproverId).getUserName();

        // 1. 명함신청 반려 처리
        BcdDisapproveRequestDTO disapproveRequest = BcdDisapproveRequestDTO.builder()
                .disapproverId(disapproverId)
                .disapprover(disapprover)
                .rejectReason(confirmRequestDTO.getRejectReason())
                .respondDate(LocalDateTime.now())
                .status("C")
                .build();

        bcdMaster.updateDisapprove(disapproveRequest);
        bcdMasterRepository.save(bcdMaster);

        // 2. 알림 전송
        //  - 명함 신청자와 대상자가 다를 경우, 대상자에게도 알림 전송
        if (!Objects.equals(bcdMaster.getDrafterId(), bcdDetail.getUserId())) {
            notificationSendService.sendBcdRejection(bcdMaster.getDraftDate(), bcdDetail.getUserId());
        }
        notificationSendService.sendBcdRejection(bcdMaster.getDraftDate(), bcdMaster.getDrafterId());

        // 3. 팀장, 파트장, 본부장 -> ADMIN 권한 및 사이드바 권한 취소 여부 결정
        StdGroup stdGroup1 = stdGroupRepository.findByGroupCd("C002")
                .orElseThrow(() -> new IllegalArgumentException("Not Found"));

        String instCd = infoService.getUserInfoDetail(bcdMaster.getCurrentApproverId()).getInstCd();
        List<StdDetail> stdDetail1 = stdDetailRepository.findByGroupCdAndEtcItem1(stdGroup1, instCd)
                .orElseThrow(() -> new IllegalArgumentException("Not Found"));

        boolean userIdExistsInStdDetail = stdDetail1.stream()
                .anyMatch(detail -> bcdMaster.getCurrentApproverId().equals(detail.getEtcItem2()) ||
                        bcdMaster.getCurrentApproverId().equals(detail.getEtcItem3()));

        if (userIdExistsInStdDetail) {
            return;
        }

        // 권한 취소 여부 결정
        List<BcdMaster> bcdMasterList = bcdMasterRepository.findAllByStatusAndCurrentApproverIndex("A", 0)
                .orElseThrow(() -> new IllegalArgumentException("Not Found"));
        List<DocMaster> docMasterList = docMasterRepository.findAllByStatusAndCurrentApproverIndex("A", 0)
                .orElseThrow(() -> new IllegalArgumentException("Not Found"));

        boolean shouldCancelAdminByBcd = true;
        boolean shouldCancelAdminByDoc = true;

        for (BcdMaster master : bcdMasterList) {
            if (master.getCurrentApproverId().equals(disapproverId)) {
                shouldCancelAdminByBcd = false;
                break;
            }
        }

        for (DocMaster master : docMasterList) {
            if (master.getCurrentApproverId().equals(disapproverId)) {
                shouldCancelAdminByDoc = false;
                break;
            }
        }

        if (shouldCancelAdminByBcd && shouldCancelAdminByDoc) {
            Authority authority = authorityRepository.findByUserIdAndDeletedtIsNull(bcdMaster.getCurrentApproverId())
                    .orElseThrow(() -> new IllegalArgumentException("Not Found2"));

            // ADMIN 권한 취소
            authority.deleteAdmin(LocalDateTime.now());
            authorityRepository.save(authority);

            // 사이드바 권한 취소
            StdGroup stdGroup2 = stdGroupRepository.findByGroupCd("B002")
                    .orElseThrow(() -> new IllegalArgumentException("Not Found3"));
            StdDetail stdDetail2 = stdDetailRepository.findByGroupCdAndDetailCd(stdGroup2, bcdMaster.getCurrentApproverId())
                    .orElseThrow(() -> new IllegalArgumentException("Not Found4"));

            stdDetailRepository.delete(stdDetail2);
        }
    }

    /*신청이력조회*/
    @Override
    @Transactional(readOnly = true)
    public List<BcdHistoryResponseDTO> getBcdApplicationHistory(LocalDateTime startDate, LocalDateTime endDate, String draftId) {

//        Timestamp[] timestamps = getDateIntoTimestamp(startDate, endDate);

        BcdMaster bcdMaster = bcdMasterRepository.findById(draftId)
                .orElseThrow(() -> new EntityNotFoundException("BcdMaster not found for draft ID: " + draftId));

        String drafterId = bcdMaster.getDrafterId();

        List<BcdMaster> bcdMasters = bcdMasterRepository.findAllByDrafterIdAndDraftDateBetweenOrderByDraftDateDesc(drafterId, startDate, endDate)
                .orElseThrow(() -> new EntityNotFoundException("BcdMaster not found for draft ID: " + drafterId));

        Map<String, BcdDetail> bcdDetailMap = new HashMap<>();

        for (BcdMaster master : bcdMasters) {
            BcdDetail bcdDetail = bcdDetailRepository.findById(master.getDraftId())
                    .orElseThrow(() -> new EntityNotFoundException("BcdDetail not found for BcdMaster ID: " + master.getDraftId()));
            bcdDetailMap.put(master.getDraftId(), bcdDetail);
        }

        return bcdMasters.stream().map(master -> {
            BcdDetail bcdDetail = bcdDetailMap.get(master.getDraftId());

            return BcdHistoryResponseDTO.builder()
                    .title(master.getTitle())
                    .draftDate(master.getDraftDate())
                    .applyStatus(master.getStatus())
                    .quantity(bcdDetail != null ? bcdDetail.getQuantity() : null)
                    .build();
        }).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<BcdHistoryResponseDTO> getBcdApplicationHistory2(PostSearchRequestDTO postSearchRequestDTO, Pageable page, String draftId) {
        return bcdApplyQueryRepositoryImpl.getBcdApplicationHistory(postSearchRequestDTO, page, draftId);
    }

//    public static Timestamp[] getDateIntoTimestamp(LocalDateTime startDate, LocalDateTime endDate) {
//
//        if (startDate == null) {
//            startDate = LocalDate.now().minusMonths(1);
//        }
//        if (endDate == null) {
//            endDate = LocalDate.now();
//        }
//
//        Timestamp startTimestamp = Timestamp.valueOf(startDate.atStartOfDay());
//        Timestamp endTimestamp = Timestamp.valueOf(endDate.atTime(LocalTime.MAX));
//
//        return new Timestamp[]{startTimestamp, endTimestamp};
//    }
}
