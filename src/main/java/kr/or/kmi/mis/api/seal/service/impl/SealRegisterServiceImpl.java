package kr.or.kmi.mis.api.seal.service.impl;

import kr.or.kmi.mis.api.seal.model.entity.SealRegisterDetail;
import kr.or.kmi.mis.api.seal.model.request.SealRegisterRequestDTO;
import kr.or.kmi.mis.api.seal.model.request.SealUpdateRequestDTO;
import kr.or.kmi.mis.api.seal.repository.SealRegisterDetailRepository;
import kr.or.kmi.mis.api.seal.service.SealRegisterHistoryService;
import kr.or.kmi.mis.api.seal.service.SealRegisterService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;

@Service
@RequiredArgsConstructor
public class SealRegisterServiceImpl implements SealRegisterService {

    private final SealRegisterDetailRepository sealRegisterDetailRepository;
    private final SealRegisterHistoryService sealRegisterHistoryService;

    @Override
    @Transactional
    public void registerSeal(SealRegisterRequestDTO sealRegisterRequestDTO) {

        SealRegisterDetail sealRegisterDetail = sealRegisterRequestDTO.toDetailEntity();
        sealRegisterDetail.setRgstrId(sealRegisterRequestDTO.getDrafterId());
        sealRegisterDetail.setRgstDt(new Timestamp(System.currentTimeMillis()));
        sealRegisterDetailRepository.save(sealRegisterDetail);
    }

    @Override
    @Transactional
    public void updateSeal(Long draftId, SealUpdateRequestDTO sealUpdateRequestDTO) {

        SealRegisterDetail sealRegisterDetail = sealRegisterDetailRepository.findById(draftId)
                .orElseThrow(() -> new IllegalArgumentException("Not Found"));

        sealRegisterHistoryService.createSealRegisterHistory(sealRegisterDetail);

        sealRegisterDetail.update(sealUpdateRequestDTO);
        sealRegisterDetail.setUpdtrId(sealUpdateRequestDTO.getDrafterId());
        sealRegisterDetail.setUpdtDt(new Timestamp(System.currentTimeMillis()));
        sealRegisterDetailRepository.save(sealRegisterDetail);
    }

    @Override
    @Transactional
    public void deleteSeal(Long draftId) {

        // TODO: 삭제한거도 History에 남겨야하나..?
        sealRegisterDetailRepository.deleteById(draftId);
    }
}
