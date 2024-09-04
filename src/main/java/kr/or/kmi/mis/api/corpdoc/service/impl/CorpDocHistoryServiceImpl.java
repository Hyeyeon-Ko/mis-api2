package kr.or.kmi.mis.api.corpdoc.service.impl;

import kr.or.kmi.mis.api.corpdoc.model.entity.CorpDocDetail;
import kr.or.kmi.mis.api.corpdoc.model.entity.CorpDocHistory;
import kr.or.kmi.mis.api.corpdoc.repository.CorpDocHistoryRepository;
import kr.or.kmi.mis.api.corpdoc.service.CorpDocHistoryService;
import kr.or.kmi.mis.api.doc.repository.DocHistoryRepository;
import kr.or.kmi.mis.api.user.service.InfoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;

@Service
@RequiredArgsConstructor
public class CorpDocHistoryServiceImpl implements CorpDocHistoryService {

    private final CorpDocHistoryRepository corpDocHistoryRepository;
    private final InfoService infoService;
    private final DocHistoryRepository docHistoryRepository;

    @Override
    @Transactional
    public void createCorpDocHistory(CorpDocDetail corpDocDetail) {

        Long maxSeqId = corpDocHistoryRepository.findTopByDraftIdOrderBySeqIdDesc(corpDocDetail.getDraftId())
                .map(CorpDocHistory::getSeqId).orElse(0L);

        CorpDocHistory corpDocHistory = CorpDocHistory.builder()
                .corpDocDetail(corpDocDetail)
                .seqId(maxSeqId+1)
                .build();

        corpDocHistory.setRgstrId(corpDocDetail.getRgstrId());
        corpDocHistory.setRgstDt(corpDocDetail.getRgstDt());
        corpDocHistory.setUpdtrId(infoService.getUserInfo().getUserId());
        corpDocHistory.setUpdtDt(new Timestamp(System.currentTimeMillis()));

        corpDocHistoryRepository.save(corpDocHistory);
    }
}
