package kr.or.kmi.mis.api.doc.service.impl;

import kr.or.kmi.mis.api.doc.model.entity.DocDetail;
import kr.or.kmi.mis.api.doc.model.entity.DocHistory;
import kr.or.kmi.mis.api.doc.repository.DocHistoryRepository;
import kr.or.kmi.mis.api.doc.service.DocHistoryService;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;

@Service
@RequiredArgsConstructor
public class DocHistoryServiceImpl implements DocHistoryService {

    private final DocHistoryRepository docHistoryRepository;

    @Override
    @Transactional
    public void createDocHistory(DocDetail docDetail) {

        Long maxSeqId = docHistoryRepository.findTopByDraftIdOrderBySeqIdDesc(docDetail.getDraftId())
                .map(DocHistory::getSeqId).orElse(0L);

        // DocHistory 객체 생성
        DocHistory docHistory = DocHistory.builder()
                .docDetail(docDetail)
                .seqId(maxSeqId+1)
                .build();

        docHistory.setUpdtDt(new Timestamp(System.currentTimeMillis()));

        // DocHistory 객체를 저장
        docHistoryRepository.save(docHistory);
    }
}
