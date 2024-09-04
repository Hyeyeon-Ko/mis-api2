package kr.or.kmi.mis.api.seal.service.impl;

import kr.or.kmi.mis.api.seal.model.entity.SealExportDetail;
import kr.or.kmi.mis.api.seal.model.entity.SealExportHistory;
import kr.or.kmi.mis.api.seal.repository.SealExportHistoryRepository;
import kr.or.kmi.mis.api.seal.service.SealExportHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SealExportHistoryServiceImpl implements SealExportHistoryService {

    private final SealExportHistoryRepository sealExportHistoryRepository;

    @Override
    @Transactional
    public void createSealExportHistory(SealExportDetail sealExportDetail) {

        Long maxSeqId = sealExportHistoryRepository.findTopByDraftIdOrderBySeqIdDesc(sealExportDetail.getDraftId())
                .map(SealExportHistory::getSeqId).orElse(0L);

        SealExportHistory sealExportHistory = SealExportHistory.builder()
                .sealExportDetail(sealExportDetail)
                        .seqId(maxSeqId+1)
                .build();

        sealExportHistoryRepository.save(sealExportHistory);
    }
}
