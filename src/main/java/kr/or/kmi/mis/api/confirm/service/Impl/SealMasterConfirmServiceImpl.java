package kr.or.kmi.mis.api.confirm.service.Impl;

import kr.or.kmi.mis.api.apply.model.request.ConfirmRequestDTO;
import kr.or.kmi.mis.api.confirm.service.SealMasterConfirmService;
import kr.or.kmi.mis.api.noti.service.NotificationSendService;
import kr.or.kmi.mis.api.seal.model.entity.SealMaster;
import kr.or.kmi.mis.api.seal.repository.SealMasterRepository;
import kr.or.kmi.mis.api.user.service.InfoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SealMasterConfirmServiceImpl implements SealMasterConfirmService {

    private final SealMasterRepository sealMasterRepository;
    private final InfoService infoService;
    private final NotificationSendService notificationSendService;

    @Override
    @Transactional
    public void approve(String draftId, ConfirmRequestDTO confirmRequestDTO) {

        // 1. 인장신청 승인
        SealMaster sealMaster = sealMasterRepository.findById(draftId)
                .orElseThrow(() -> new IllegalArgumentException("Not Found"));

        System.out.println("userId = " + confirmRequestDTO.getUserId());
        String approver = infoService.getUserInfoDetail(confirmRequestDTO.getUserId()).getUserName();

        sealMaster.confirm("E", approver, confirmRequestDTO.getUserId());
        sealMasterRepository.save(sealMaster);

        // 2. 알림 전송
        notificationSendService.sendSealApproval(sealMaster.getDraftDate(), sealMaster.getDrafterId());
    }

    @Override
    @Transactional
    public void disapprove(String draftId, ConfirmRequestDTO confirmRequestDTO) {

        // 1. 인장신청 반려
        SealMaster sealMaster = sealMasterRepository.findById(draftId)
                .orElseThrow(() -> new IllegalArgumentException("Not Found"));

        String disapprover = infoService.getUserInfoDetail(confirmRequestDTO.getUserId()).getUserName();

        sealMaster.reject("C", disapprover, confirmRequestDTO.getUserId(), confirmRequestDTO.getRejectReason());
        sealMasterRepository.save(sealMaster);

        // 2. 알림 전송
        notificationSendService.sendSealDisapproval(sealMaster.getDraftDate(), sealMaster.getDrafterId());
    }
}
