package kr.or.kmi.mis.api.seal.service.impl;

import kr.or.kmi.mis.api.seal.model.entity.SealImprintDetail;
import kr.or.kmi.mis.api.seal.model.entity.SealImprintHistory;
import kr.or.kmi.mis.api.seal.repository.SealImprintHistoryRepository;
import kr.or.kmi.mis.api.seal.service.SealImprintHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class SealImprintHistoryServiceImpl implements SealImprintHistoryService {

    private final SealImprintHistoryRepository sealImprintHistoryRepository;

    @Override
    @Transactional
    public void createSealImprintHistory(SealImprintDetail sealImprintDetail) {

        Long maxSeqId = sealImprintHistoryRepository.findTopByDraftIdOrderBySeqIdDesc(sealImprintDetail.getDraftId())
                .map(SealImprintHistory::getSeqId).orElse(0L);

        SealImprintHistory sealImprintHistory = SealImprintHistory.builder()
                .sealImprintDetail(sealImprintDetail)
                .seqId(maxSeqId+1)
                .build();

        sealImprintHistory.setUpdtDt(LocalDateTime.now());

        sealImprintHistoryRepository.save(sealImprintHistory);
    }
}
