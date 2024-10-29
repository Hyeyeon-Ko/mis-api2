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
import kr.or.kmi.mis.api.toner.repository.TonerMasterRepository;
import kr.or.kmi.mis.api.user.service.InfoService;
import kr.or.kmi.mis.cmm.model.request.PostSearchRequestDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

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
    private final TonerMasterRepository tonerMasterRepository;

    /**
     * BcdMaster 엔티티를 draftId로 조회하여 반환.
     * @param draftId 결재 문서 ID
     * @return BcdMaster 엔티티
     */
    private BcdMaster getBcdMaster(String draftId) {
        return bcdMasterRepository.findById(draftId)
                .orElseThrow(() -> new EntityNotFoundException("BcdMaster not found for draft ID: " + draftId));
    }

    /**
     * BcdDetail 엔티티를 draftId로 조회하여 반환.
     * @param draftId 결재 문서 ID
     * @return BcdDetail 엔티티
     */
    private BcdDetail getBcdDetail(String draftId) {
        return bcdDetailRepository.findById(draftId)
                .orElseThrow(() -> new EntityNotFoundException("BcdDetail not found for draft ID: " + draftId));
    }

    /**
     * 결재 상세 정보를 반환.
     * @param draftId 결재 문서 ID
     * @return BcdDetailResponseDTO 결재 상세 정보 DTO
     */
    @Override
    @Transactional(readOnly = true)
    public BcdDetailResponseDTO getBcdDetailInfo(String draftId) {
        BcdMaster bcdMaster = getBcdMaster(draftId);
        BcdDetail bcdDetail = getBcdDetail(draftId);

        String drafter = bcdMaster.getDrafter();
        List<String> names = stdBcdService.getBcdStdNames(bcdDetail);

        return BcdDetailResponseDTO.of(bcdDetail, drafter, names);
    }

    /**
     * 결재 승인 처리.
     * @param draftId 결재 문서 ID
     * @param confirmRequestDTO 결재 요청 데이터
     */
    @Override
    @Transactional
    public void approve(String draftId, ConfirmRequestDTO confirmRequestDTO) {

        // 1. 승인
        BcdMaster bcdMaster = getBcdMaster(draftId);
        validateApprover(bcdMaster, confirmRequestDTO.getUserId());
        boolean isLastApprover = checkLastApprover(bcdMaster);

        String approverId = confirmRequestDTO.getUserId();
        String approver = infoService.getUserInfoDetail(approverId).getUserName();

        BcdApproveRequestDTO approveRequest = createApproveRequest(approverId, approver, isLastApprover);
        bcdMaster.updateCurrentApproverIndex(bcdMaster.getCurrentApproverIndex() + 1);
        bcdMaster.updateApprove(approveRequest);
        bcdMasterRepository.save(bcdMaster);

        // 2. 권한 취소 여부 결정
        String instCd = infoService.getUserInfoDetail(approverId).getInstCd();
        if (existsInStdDetail(approverId, "B005", instCd)) {
            return;
        }

        // 3. 결재자들의 권한 취소 여부 체크 및 처리
        List<BcdMaster> bcdMasterList = bcdMasterRepository.findAllByStatusAndCurrentApproverIndex("A", 0)
                .orElseThrow(() -> new IllegalArgumentException("Not Found"));
        List<DocMaster> docMasterList = docMasterRepository.findAllByStatusAndCurrentApproverIndex("A", 0)
                .orElseThrow(() -> new IllegalArgumentException("Not Found"));

        boolean shouldCancelAdminByBcd = shouldCancelAdmin(bcdMasterList, approverId);
        boolean shouldCancelAdminByDoc = shouldCancelAdmin(docMasterList, approverId);

        if (shouldCancelAdminByBcd && shouldCancelAdminByDoc) {
            cancelAdminAndSidebarAuthorities(approverId);
        }
    }

    /**
     * 결재 반려 처리.
     * @param draftId 결재 문서 ID
     * @param confirmRequestDTO 결재 요청 데이터
     */
    @Override
    @Transactional
    public void disapprove(String draftId, ConfirmRequestDTO confirmRequestDTO) {

        BcdMaster bcdMaster = getBcdMaster(draftId);
        BcdDetail bcdDetail = getBcdDetail(draftId);

        String disapproverId = confirmRequestDTO.getUserId();
        String disapprover = infoService.getUserInfoDetail(disapproverId).getUserName();

        // 1. 반려 처리 요청 생성
        BcdDisapproveRequestDTO disapproveRequest = createDisapproveRequest(confirmRequestDTO, disapprover, disapproverId);
        bcdMaster.updateDisapprove(disapproveRequest);
        bcdMasterRepository.save(bcdMaster);

        // 2. 반려 알림 전송
        sendRejectionNotifications(bcdMaster, bcdDetail);

        // 3. 권한 취소 여부 결정
        String instCd = infoService.getUserInfoDetail(bcdMaster.getCurrentApproverId()).getInstCd();
        if (existsInStdDetail(bcdMaster.getCurrentApproverId(), "B005", instCd)) {
            return;
        }

        // 4. 결재자들의 권한 취소 여부 체크 및 처리
        List<BcdMaster> bcdMasterList = bcdMasterRepository.findAllByStatusAndCurrentApproverIndex("A", 0)
                .orElseThrow(() -> new IllegalArgumentException("Not Found"));
        List<DocMaster> docMasterList = docMasterRepository.findAllByStatusAndCurrentApproverIndex("A", 0)
                .orElseThrow(() -> new IllegalArgumentException("Not Found"));

        boolean shouldCancelAdminByBcd = shouldCancelAdmin(bcdMasterList, disapproverId);
        boolean shouldCancelAdminByDoc = shouldCancelAdmin(docMasterList, disapproverId);

        if (shouldCancelAdminByBcd && shouldCancelAdminByDoc) {
            cancelAdminAndSidebarAuthorities(bcdMaster.getCurrentApproverId());
        }
    }

    /**
     * 승인 요청 생성
     * @param approverId 결재자 ID
     * @param approver 결재자 이름
     * @param isLastApprover 마지막 결재자인지 여부
     * @return BcdApproveRequestDTO 승인 요청
     */
    private BcdApproveRequestDTO createApproveRequest(String approverId, String approver, boolean isLastApprover) {
        return BcdApproveRequestDTO.builder()
                .approverId(approverId)
                .approver(approver)
                .respondDate(LocalDateTime.now())
                .status(isLastApprover ? "B" : "A")
                .build();
    }

    /**
     * 결재자가 맞는지 검증
     * @param bcdMaster 결재 마스터 엔티티
     * @param userId 검증할 사용자 ID
     */
    private void validateApprover(BcdMaster bcdMaster, String userId) {
        if (!bcdMaster.getCurrentApproverId().equals(userId)) {
            throw new IllegalArgumentException("현재 결재자가 아닙니다.");
        }
    }

    /**
     * 마지막 결재자인지 확인
     * @param bcdMaster 결재 마스터 엔티티
     * @return 마지막 결재자 여부
     */
    private boolean checkLastApprover(BcdMaster bcdMaster) {
        return bcdMaster.getCurrentApproverIndex() == bcdMaster.getApproverChain().split(", ").length - 1;
    }

    /**
     * 결재자의 StdDetail 데이터가 존재하는지 확인
     * @param approverId 결재자 ID
     * @param groupCd 그룹 코드
     * @param instCd 기관 코드
     * @return 존재 여부
     */
    private boolean existsInStdDetail(String approverId, String groupCd, String instCd) {
        StdGroup stdGroup = stdGroupRepository.findByGroupCd(groupCd)
                .orElseThrow(() -> new IllegalArgumentException("StdGroup not found"));

        List<StdDetail> stdDetails = stdDetailRepository.findByGroupCdAndEtcItem1(stdGroup, instCd)
                .orElseThrow(() -> new IllegalArgumentException("StdDetail not found"));

        return stdDetails.stream()
                .anyMatch(detail -> approverId.equals(detail.getEtcItem2()) || approverId.equals(detail.getEtcItem3()));
    }

    /**
     * 권한 취소 여부 결정
     * @param masterList 결재 마스터 리스트
     * @param approverId 결재자 ID
     * @return 권한 취소 여부
     */
    private boolean shouldCancelAdmin(List<?> masterList, String approverId) {
        return masterList.stream().noneMatch(master -> {
            if (master instanceof BcdMaster) {
                return ((BcdMaster) master).getCurrentApproverId().equals(approverId);
            } else if (master instanceof DocMaster) {
                return ((DocMaster) master).getCurrentApproverId().equals(approverId);
            }
            return false;
        });
    }

    /**
     * ADMIN 및 사이드바 권한 취소
     * @param approverId 결재자 ID
     */
    private void cancelAdminAndSidebarAuthorities(String approverId) {

        // ADMIN 권한 취소
        Authority authority = authorityRepository.findByUserId(approverId)
                .orElseThrow(() -> new IllegalArgumentException("Not Found"));

        authorityRepository.delete(authority);

        // 사이드바 권한 취소
        StdGroup stdGroup = stdGroupRepository.findByGroupCd("B002")
                .orElseThrow(() -> new IllegalArgumentException("Not Found"));
        StdDetail stdDetail = stdDetailRepository.findByGroupCdAndDetailCd(stdGroup, approverId)
                .orElseThrow(() -> new IllegalArgumentException("Not Found"));

        stdDetailRepository.delete(stdDetail);
    }

    /**
     * 반려 요청 생성
     * @param confirmRequestDTO 반려 요청 데이터
     * @param disapprover 반려자 이름
     * @param disapproverId 반려자 ID
     * @return BcdDisapproveRequestDTO 반려 요청
     */
    private BcdDisapproveRequestDTO createDisapproveRequest(ConfirmRequestDTO confirmRequestDTO, String disapprover, String disapproverId) {
        return BcdDisapproveRequestDTO.builder()
                .disapproverId(disapproverId)
                .disapprover(disapprover)
                .rejectReason(confirmRequestDTO.getRejectReason())
                .respondDate(LocalDateTime.now())
                .status("C")
                .build();
    }

    /**
     * 반려 알림 전송
     * @param bcdMaster 결재 마스터 엔티티
     * @param bcdDetail 결재 상세 엔티티
     */
    private void sendRejectionNotifications(BcdMaster bcdMaster, BcdDetail bcdDetail) {
        if (!Objects.equals(bcdMaster.getDrafterId(), bcdDetail.getUserId())) {
            notificationSendService.sendBcdRejection(bcdMaster.getDraftDate(), bcdDetail.getUserId());
        }
        notificationSendService.sendBcdRejection(bcdMaster.getDraftDate(), bcdMaster.getDrafterId());
    }

    /**
     * 신청 이력 조회
     * @param postSearchRequestDTO 검색 요청 데이터
     * @param page 페이지 정보
     * @param draftId 결재 문서 ID
     * @return 신청 이력 페이지
     */
    @Override
    @Transactional(readOnly = true)
    public Page<BcdHistoryResponseDTO> getBcdApplicationHistory2(PostSearchRequestDTO postSearchRequestDTO, Pageable page, String draftId) {
        return bcdApplyQueryRepositoryImpl.getBcdApplicationHistory(postSearchRequestDTO, page, draftId);
    }
}
