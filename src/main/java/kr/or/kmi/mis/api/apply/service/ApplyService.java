package kr.or.kmi.mis.api.apply.service;

import kr.or.kmi.mis.api.apply.model.response.ApplyResponseDTO;
import kr.or.kmi.mis.api.apply.model.response.MyApplyResponseDTO;
import kr.or.kmi.mis.api.apply.model.response.PendingResponseDTO;

import java.time.LocalDate;

public interface ApplyService {

    public ApplyResponseDTO getAllApplyList(String documentType, LocalDate startDate, LocalDate endDate);
    public MyApplyResponseDTO getAllMyApplyList(String documentType, LocalDate startDate, LocalDate endDate);
    public PendingResponseDTO getAllPendingList();
    public PendingResponseDTO getMyPendingList();
}
