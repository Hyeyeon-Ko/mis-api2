package kr.or.kmi.mis.api.corpdoc.service;

import kr.or.kmi.mis.api.corpdoc.model.entity.CorpDocDetail;

public interface CorpDocHistoryService {
    void createCorpDocHistory(CorpDocDetail corpDocDetail);
}
