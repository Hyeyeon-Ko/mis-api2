package kr.or.kmi.mis.api.confirm.service.Impl;

import kr.or.kmi.mis.api.confirm.service.SealMasterConfirmService;
import kr.or.kmi.mis.api.noti.model.response.SseResponseDTO;
import kr.or.kmi.mis.api.noti.service.NotificationSendService;
import kr.or.kmi.mis.api.noti.service.NotificationService;
import kr.or.kmi.mis.api.seal.model.entity.SealMaster;
import kr.or.kmi.mis.api.seal.repository.SealMasterRepository;
import kr.or.kmi.mis.api.user.service.InfoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;

@Service
@RequiredArgsConstructor
public class SealMasterConfirmServiceImpl implements SealMasterConfirmService {

    private final SealMasterRepository sealMasterRepository;
    private final InfoService infoService;
    private final NotificationSendService notificationSendService;

    @Override
    @Transactional
    public void approve(Long draftId) {

        // 1. 인장신청 승인
        SealMaster sealMaster = sealMasterRepository.findById(draftId)
                .orElseThrow(() -> new IllegalArgumentException("Not Found"));

        String approver = infoService.getUserInfo().getUserName();
        String approverId = infoService.getUserInfo().getUserId();

        sealMaster.confirm("E", approver, approverId);
        sealMasterRepository.save(sealMaster);

        // 2. 알림 전송
        notificationSendService.sendSealApproval(sealMaster.getDraftDate(), sealMaster.getDrafterId());
    }

    @Override
    @Transactional
    public void disapprove(Long draftId, String rejectReason) {

        // 1. 인장신청 반려
        SealMaster sealMaster = sealMasterRepository.findById(draftId)
                .orElseThrow(() -> new IllegalArgumentException("Not Found"));

        String disapprover = infoService.getUserInfo().getUserName();
        String disapproverId = infoService.getUserInfo().getUserId();

        sealMaster.reject("C", disapprover, disapproverId, rejectReason);
        sealMasterRepository.save(sealMaster);

        // 2. 알림 전송
        notificationSendService.sendSealDisapproval(sealMaster.getDraftDate(), sealMaster.getDrafterId());
    }
}
