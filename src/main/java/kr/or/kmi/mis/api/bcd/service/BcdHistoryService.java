package kr.or.kmi.mis.api.bcd.service;

import kr.or.kmi.mis.api.bcd.model.entity.BcdDetail;
import kr.or.kmi.mis.api.bcd.model.entity.BcdHistory;

public interface BcdHistoryService {

    public void createBcdHistory(BcdDetail bcdDetail);

}
