package kr.or.kmi.mis.api.main.service;

import kr.or.kmi.mis.api.main.model.response.ApplyResponseDTO;

import java.sql.Timestamp;
import java.util.List;

public interface ApplyService {

    public ApplyResponseDTO getAllApplyList(String documentType, Timestamp startDate, Timestamp endDate);
    public ApplyResponseDTO getAllMyApplyList(String documentType, Timestamp startDate, Timestamp endDate);

}
