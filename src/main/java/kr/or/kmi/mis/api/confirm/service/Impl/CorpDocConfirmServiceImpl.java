package kr.or.kmi.mis.api.confirm.service.Impl;

import kr.or.kmi.mis.api.apply.model.request.ConfirmRequestDTO;
import kr.or.kmi.mis.api.confirm.service.CorpDocConfirmService;
import kr.or.kmi.mis.api.corpdoc.model.entity.CorpDocMaster;
import kr.or.kmi.mis.api.corpdoc.repository.CorpDocMasterRepository;
import kr.or.kmi.mis.api.exception.EntityNotFoundException;
import kr.or.kmi.mis.api.noti.service.NotificationSendService;
import kr.or.kmi.mis.api.user.service.InfoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CorpDocConfirmServiceImpl implements CorpDocConfirmService {

    private final CorpDocMasterRepository corpDocMasterRepository;
    private final NotificationSendService notificationSendService;
    private final InfoService infoService;

    @Override
    @Transactional
    public void approve(String draftId, ConfirmRequestDTO confirmRequestDTO) {

        // 1. 법인서류신청 승인
        CorpDocMaster corpDocMaster = corpDocMasterRepository.findById(draftId)
                .orElseThrow(() -> new EntityNotFoundException("Not found: " + draftId));

        String approverId = confirmRequestDTO.getUserId();
        String approver = infoService.getUserInfoDetail(approverId).getUserName();

        corpDocMaster.approve(approver, approverId);

        corpDocMasterRepository.save(corpDocMaster);

        // 2. 알림 전송
        notificationSendService.sendCorpDocApproval(corpDocMaster.getDraftDate(), corpDocMaster.getDrafterId());

    }

    @Override
    @Transactional
    public void reject(String draftId, ConfirmRequestDTO confirmRequestDTO) {

        // 1. 법인서류신청 반려
        CorpDocMaster corpDocMaster = corpDocMasterRepository.findById(draftId)
                .orElseThrow(() -> new EntityNotFoundException("Not found: " + draftId));

        String disapproverId = confirmRequestDTO.getUserId();
        String disapprover = infoService.getUserInfoDetail(disapproverId).getUserName();

        corpDocMaster.disapprove(disapprover, disapproverId, corpDocMaster.getRejectReason());

        corpDocMasterRepository.save(corpDocMaster);

        // 2. 알림 전송
        notificationSendService.sendCorpDocRejection(corpDocMaster.getDraftDate(), corpDocMaster.getDrafterId());

    }
}
