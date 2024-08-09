package kr.or.kmi.mis.api.doc.service;

import kr.or.kmi.mis.api.doc.model.entity.DocDetail;

public interface DocHistoryService {

    void createDocHistory(DocDetail docDetail);
}
