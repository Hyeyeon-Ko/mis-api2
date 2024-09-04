package kr.or.kmi.mis.api.confirm.service.Impl;

import kr.or.kmi.mis.api.confirm.service.CorpDocConfirmService;
import kr.or.kmi.mis.api.corpdoc.model.entity.CorpDocMaster;
import kr.or.kmi.mis.api.corpdoc.repository.CorpDocMasterRepository;
import kr.or.kmi.mis.api.exception.EntityNotFoundException;
import kr.or.kmi.mis.api.user.service.InfoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CorpDocConfirmServiceImpl implements CorpDocConfirmService {

    private final CorpDocMasterRepository corpDocMasterRepository;
    private final InfoService infoService;

    @Override
    @Transactional
    public void approve(Long draftId) {
        CorpDocMaster corpDocMaster = corpDocMasterRepository.findById(draftId)
                .orElseThrow(() -> new EntityNotFoundException("Not found: " + draftId));

        String approver = infoService.getUserInfo().getUserName();
        String approverId = infoService.getUserInfo().getUserId();
        corpDocMaster.approve(approver, approverId);

        corpDocMasterRepository.save(corpDocMaster);
    }

    @Override
    @Transactional
    public void reject(Long draftId, String rejectReason) {
        CorpDocMaster corpDocMaster = corpDocMasterRepository.findById(draftId)
                .orElseThrow(() -> new EntityNotFoundException("Not found: " + draftId));

        String disapprover = infoService.getUserInfo().getUserName();
        String disapproverId = infoService.getUserInfo().getUserId();
        corpDocMaster.disapprove(disapprover, disapproverId, rejectReason);

        corpDocMasterRepository.save(corpDocMaster);
    }
}
