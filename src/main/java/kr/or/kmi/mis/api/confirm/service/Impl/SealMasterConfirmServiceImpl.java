package kr.or.kmi.mis.api.confirm.service.Impl;

import kr.or.kmi.mis.api.confirm.service.SealMasterConfirmService;
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

    @Override
    @Transactional
    public void approve(Long draftId) {

        SealMaster sealMaster = sealMasterRepository.findById(draftId)
                .orElseThrow(() -> new IllegalArgumentException("Not Found"));

        String approver = infoService.getUserInfo().getUserName();
        String approverId = infoService.getUserInfo().getUserId();

        sealMaster.confirm("E", approver, approverId);
        sealMasterRepository.save(sealMaster);
    }

    @Override
    @Transactional
    public void disapprove(Long draftId, String rejectReason) {

        SealMaster sealMaster = sealMasterRepository.findById(draftId)
                .orElseThrow(() -> new IllegalArgumentException("Not Found"));

        String disapprover = infoService.getUserInfo().getUserName();
        String disapproverId = infoService.getUserInfo().getUserId();

        sealMaster.reject("C", disapprover, disapproverId, rejectReason);
        sealMasterRepository.save(sealMaster);
    }
}
