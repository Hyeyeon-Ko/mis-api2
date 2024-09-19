package kr.or.kmi.mis.api.confirm.service.Impl;

import kr.or.kmi.mis.api.bcd.model.entity.BcdDetail;
import kr.or.kmi.mis.api.bcd.model.entity.BcdMaster;
import kr.or.kmi.mis.api.bcd.model.response.BcdDetailResponseDTO;
import kr.or.kmi.mis.api.bcd.repository.BcdDetailRepository;
import kr.or.kmi.mis.api.bcd.repository.BcdMasterRepository;
import kr.or.kmi.mis.api.confirm.model.request.BcdApproveRequestDTO;
import kr.or.kmi.mis.api.confirm.model.request.BcdDisapproveRequestDTO;
import kr.or.kmi.mis.api.confirm.model.response.BcdHistoryResponseDTO;
import kr.or.kmi.mis.api.confirm.service.BcdConfirmService;
import kr.or.kmi.mis.api.exception.EntityNotFoundException;
import kr.or.kmi.mis.api.noti.service.NotificationSendService;
import kr.or.kmi.mis.api.std.service.StdBcdService;
import kr.or.kmi.mis.api.user.service.InfoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
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

    @Override
    @Transactional(readOnly = true)
    public BcdDetailResponseDTO getBcdDetailInfo(Long id) {
        BcdMaster bcdMaster = bcdMasterRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("BcdMaster not found"));
        BcdDetail bcdDetail = bcdDetailRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("BcdDetail not found for draft ID: " + id));

        String drafter = bcdMaster.getDrafter();
        // 기준자료에서 각 기준자료 코드에 해당하는 명칭 불러오기
        List<String> names = stdBcdService.getBcdStdNames(bcdDetail);

        return BcdDetailResponseDTO.of(bcdDetail, drafter, names);
    }

    /* 승인 */
    @Override
    @Transactional
    public void approve(Long id, String userId) {
        BcdMaster bcdMaster = bcdMasterRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("BcdMaster not found for draft ID: " + id));

        if (!bcdMaster.getCurrentApproverId().equals(userId)) {
            throw new IllegalArgumentException("현재 결재자가 아닙니다.");
        }
        boolean isLastApprover = bcdMaster.getCurrentApproverIndex() == bcdMaster.getApproverChain().split(", ").length - 1;

        BcdApproveRequestDTO approveRequest = BcdApproveRequestDTO.builder()
                .approverId(userId)
                .approver(infoService.getUserInfo().getUserName())
                .respondDate(new Timestamp(System.currentTimeMillis()))
                .status(isLastApprover ? "B" : "A")  // 마지막 결재자라면 B(완료), 아니면 A(승인 대기)
                .build();

        if (isLastApprover) {
            bcdMaster.updateCurrentApproverIndex(bcdMaster.getCurrentApproverIndex() + 1);
            bcdMaster.updateApprove(approveRequest);
        } else {
            bcdMaster.updateCurrentApproverIndex(bcdMaster.getCurrentApproverIndex() + 1);
            bcdMaster.updateApprove(approveRequest);
        }

        bcdMasterRepository.save(bcdMaster);
    }

    /* 반려 */
    @Override
    @Transactional
    public void disapprove(Long id, String rejectReason) {
        BcdMaster bcdMaster = bcdMasterRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("BcdMaster not found for draft ID: " + id));
        BcdDetail bcdDetail = bcdDetailRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("BcdDetail not found for draft ID: " + id));

        // 1. 명함신청 반려 처리
        BcdDisapproveRequestDTO disapproveRequest = BcdDisapproveRequestDTO.builder()
                .disapproverId(infoService.getUserInfo().getUserId())
                .disapprover(infoService.getUserInfo().getUserName())
                .rejectReason(rejectReason)
                .respondDate(new Timestamp(System.currentTimeMillis()))
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

    }

    /*신청이력조회*/
    @Override
    @Transactional(readOnly = true)
    public List<BcdHistoryResponseDTO> getBcdApplicationHistory(Long draftId) {
        BcdMaster bcdMaster = bcdMasterRepository.findById(draftId)
                .orElseThrow(() -> new EntityNotFoundException("BcdMaster not found for draft ID: " + draftId));

        String drafterId = bcdMaster.getDrafterId();

        List<BcdMaster> bcdMasters = bcdMasterRepository.findAllByDrafterId(drafterId)
                .orElseThrow(() -> new EntityNotFoundException("BcdMaster not found for draft ID: " + drafterId));

        Map<Long, BcdDetail> bcdDetailMap = new HashMap<>();

        for (BcdMaster master : bcdMasters) {
            BcdDetail bcdDetail = bcdDetailRepository.findById(master.getDraftId())
                    .orElseThrow(() -> new EntityNotFoundException("BcdDetail not found for BcdMaster ID: " + master.getDraftId()));
            bcdDetailMap.put(master.getDraftId(), bcdDetail);
        }

        return bcdMasters.stream().map(master -> {
            BcdDetail bcdDetail = bcdDetailMap.get(master.getDraftId());

            return BcdHistoryResponseDTO.builder()
                    .title(master.getTitle())
                    .draftDate(master.getDraftDate().toString())
                    .applyStatus(master.getStatus())
                    .quantity(bcdDetail != null ? bcdDetail.getQuantity() : null)
                    .build();
        }).collect(Collectors.toList());
    }
}
