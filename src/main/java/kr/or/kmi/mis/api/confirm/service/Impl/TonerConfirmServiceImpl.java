package kr.or.kmi.mis.api.confirm.service.Impl;

import kr.or.kmi.mis.api.apply.model.request.ConfirmRequestDTO;
import kr.or.kmi.mis.api.confirm.service.TonerConfirmService;
import kr.or.kmi.mis.api.exception.EntityNotFoundException;
import kr.or.kmi.mis.api.noti.service.NotificationSendService;
import kr.or.kmi.mis.api.toner.model.entity.TonerMaster;
import kr.or.kmi.mis.api.toner.model.request.TonerApproverRequestDTO;
import kr.or.kmi.mis.api.toner.model.request.TonerConfirmRequestDTO;
import kr.or.kmi.mis.api.toner.model.request.TonerDisApproverRequestDTO;
import kr.or.kmi.mis.api.toner.repository.TonerMasterRepository;
import kr.or.kmi.mis.api.user.service.InfoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TonerConfirmServiceImpl implements TonerConfirmService {

    private final InfoService infoService;
    private final NotificationSendService notificationSendService;
    private final TonerMasterRepository tonerMasterRepository;

    /**
     * TonerMaster 엔티티를 draftId로 조회하여 반환.
     * @param draftIds 결재 문서 ID
     * @return TonerMaster 엔티티
     */
    private List<TonerMaster> getTonerMaster(List<String> draftIds) {
        return tonerMasterRepository.findAllByDraftIdIn(draftIds)
                .orElseThrow(() -> new EntityNotFoundException("BcdMaster not found for draft ID: " + draftIds));
    }

    /**
     * 결재 일괄 승인 처리.
     * @param tonerConfirmRequestDTO 결재 요청 데이터
     */
    @Override
    @Transactional
    public void approve(TonerConfirmRequestDTO tonerConfirmRequestDTO) {

        // 1. 승인 처리
        List<TonerMaster> tonerMasterList = getTonerMaster(tonerConfirmRequestDTO.getDraftIds());
        String approverId = tonerConfirmRequestDTO.getConfirmRequestDTO().getUserId();
        String approver = infoService.getUserInfoDetail(approverId).getUserName();

        TonerApproverRequestDTO approveRequest = createApproveRequest(approverId, approver);
        tonerMasterList.forEach(tonerMaster -> tonerMaster.updateApprove(approveRequest));
        tonerMasterRepository.saveAll(tonerMasterList);

        // 2. 승인 알림 전송
        sendApprovalNotifications(tonerMasterList);
    }

    /**
     * 승인 알림 전송.
     * @param tonerMasterList
     */
    private void sendApprovalNotifications(List<TonerMaster> tonerMasterList) {
        for (TonerMaster tonerMaster : tonerMasterList) {
            notificationSendService.sendTonerApproval(tonerMaster.getDraftDate(), tonerMaster.getDrafterId());
        }
    }

    /**
     * 결재 일괄 반려 처리.
     * @param tonerConfirmRequestDTO 결재 요청 데이터
     */
    @Override
    @Transactional
    public void disapprove(TonerConfirmRequestDTO tonerConfirmRequestDTO) {

        // 1. 반려 처리
        List<TonerMaster> tonerMasterList = getTonerMaster(tonerConfirmRequestDTO.getDraftIds());

        String disapproverId = tonerConfirmRequestDTO.getConfirmRequestDTO().getUserId();
        String disappover = infoService.getUserInfoDetail(disapproverId).getUserName();

        TonerDisApproverRequestDTO disApproverRequest = createDisapproverRequest(tonerConfirmRequestDTO.getConfirmRequestDTO(), disappover, disapproverId);
        tonerMasterList.forEach(tonerMaster -> tonerMaster.updateDisapprove(disApproverRequest));
        tonerMasterRepository.saveAll(tonerMasterList);

        // 2. 반려 알림 전송
        sendRejectionNotifications(tonerMasterList);
    }

    /**
     * 반려 알림 전송.
     * @param tonerMasterList
     */
    private void sendRejectionNotifications(List<TonerMaster> tonerMasterList) {
        for (TonerMaster tonerMaster : tonerMasterList) {
            notificationSendService.sendTonerRejection(tonerMaster.getDraftDate(), tonerMaster.getDrafterId());
        }
    }

    /**
     * 승인 요청 생성.
     * @param approverId 결재자 ID
     * @param approver 결재자 이름
     * @return TonerApproverRequestDTO 승인 요청
     */
    private TonerApproverRequestDTO createApproveRequest(String approverId, String approver) {
        return TonerApproverRequestDTO.builder()
                .approverId(approverId)
                .approver(approver)
                .respondDate(LocalDateTime.now())
                .status("B")
                .build();
    }

    /**
     * 반려 요청 생성.
     * @param confirmRequestDTO 반려 요청 데이터
     * @param disapprover 반려자 이름
     * @param disapproverId 반려자 ID
     * @return TonerDisapproveRequestDTO 반려 요청
     */
    private TonerDisApproverRequestDTO createDisapproverRequest(ConfirmRequestDTO confirmRequestDTO, String disapprover, String disapproverId) {
        return TonerDisApproverRequestDTO.builder()
                .disapproverId(disapproverId)
                .disapprover(disapprover)
                .rejectReason(confirmRequestDTO.getRejectReason())
                .respondDate(LocalDateTime.now())
                .status("C")
                .build();
    }

}
