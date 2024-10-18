package kr.or.kmi.mis.api.seal.service.impl;

import kr.or.kmi.mis.api.seal.model.entity.SealRegisterDetail;
import kr.or.kmi.mis.api.seal.model.entity.SealRegisterHistory;
import kr.or.kmi.mis.api.seal.repository.SealRegisterHistoryRepository;
import kr.or.kmi.mis.api.seal.service.SealRegisterHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class SealRegisterHistoryServiceImpl implements SealRegisterHistoryService {

    private final SealRegisterHistoryRepository sealRegisterHistoryRepository;


    @Override
    @Transactional
    public void createSealRegisterHistory(SealRegisterDetail sealRegisterDetail) {

        Long maxSeqId = sealRegisterHistoryRepository.findTopByDraftIdOrderBySeqIdDesc(sealRegisterDetail.getDraftId())
                .map(SealRegisterHistory::getSeqId).orElse(0L);

        SealRegisterHistory sealRegisterHistory = SealRegisterHistory.builder()
                .sealRegisterDetail(sealRegisterDetail)
                .seqId(maxSeqId + 1)
                .build();

        sealRegisterHistory.update(LocalDateTime.now());

        sealRegisterHistoryRepository.save(sealRegisterHistory);


    }
}
