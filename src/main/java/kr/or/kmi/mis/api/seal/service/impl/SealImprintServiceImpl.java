package kr.or.kmi.mis.api.seal.service.impl;

import kr.or.kmi.mis.api.seal.model.entity.SealImprintDetail;
import kr.or.kmi.mis.api.seal.model.entity.SealMaster;
import kr.or.kmi.mis.api.seal.model.request.ImprintRequestDTO;
import kr.or.kmi.mis.api.seal.model.request.ImprintUpdateRequestDTO;
import kr.or.kmi.mis.api.seal.repository.SealImprintDetailRepository;
import kr.or.kmi.mis.api.seal.repository.SealMasterRepository;
import kr.or.kmi.mis.api.seal.service.SealImprintHistoryService;
import kr.or.kmi.mis.api.seal.service.SealImprintService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;

@Service
@RequiredArgsConstructor
public class SealImprintServiceImpl implements SealImprintService {

    private final SealMasterRepository sealMasterRepository;
    private final SealImprintDetailRepository sealImprintDetailRepository;
    private final SealImprintHistoryService sealImprintHistoryService;

    @Override
    @Transactional
    public void applyImprint(ImprintRequestDTO imprintRequestDTO) {

        SealMaster sealMaster = imprintRequestDTO.toMasterEntity();
        sealMaster = sealMasterRepository.save(sealMaster);

        Long draftId = sealMaster.getDraftId();
        SealImprintDetail sealImprintDetail = imprintRequestDTO.toDetailEntity(draftId);
        sealImprintDetailRepository.save(sealImprintDetail);
    }

    @Override
    public void updateImprint(Long draftId, ImprintUpdateRequestDTO imprintUpdateRequestDTO) {

        SealMaster sealMaster = sealMasterRepository.findById(draftId)
                .orElseThrow(() -> new IllegalArgumentException("Not Found"));

        // 날인신청 상세 조회
        SealImprintDetail sealImprintDetailInfo = sealImprintDetailRepository.findById(draftId)
                .orElseThrow(() -> new IllegalArgumentException("Not Found"));

        // 날인신청 히스토리 저장
        sealImprintHistoryService.createSealImprintHistory(sealImprintDetailInfo);

        // 날인신청 수정사항 저장
        sealImprintDetailInfo.update(imprintUpdateRequestDTO);
        sealMasterRepository.save(sealMaster);

        sealMaster.setUpdtDt(new Timestamp(System.currentTimeMillis()));
        sealMaster.setUpdtrId(sealMaster.getDrafterId());
        sealImprintDetailInfo.setUpdtDt(new Timestamp(System.currentTimeMillis()));
    }

    @Override
    public void cancelImprint(Long draftId) {

        SealMaster sealMaster = sealMasterRepository.findById(draftId)
                .orElseThrow(() -> new IllegalArgumentException("Not Found"));

        sealMaster.updateStatus("F");
    }
}
