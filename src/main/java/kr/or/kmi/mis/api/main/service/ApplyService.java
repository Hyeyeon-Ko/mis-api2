package kr.or.kmi.mis.api.main.service;

import kr.or.kmi.mis.api.main.model.response.ApplyResponseDTO;
import kr.or.kmi.mis.api.main.model.response.PendingResponseDTO;

import java.time.LocalDate;

public interface ApplyService {

    public ApplyResponseDTO getAllApplyList(String documentType, LocalDate startDate, LocalDate endDate);
    public ApplyResponseDTO getAllMyApplyList(String documentType, LocalDate startDate, LocalDate endDate);
    public PendingResponseDTO getAllPendingList();
    public PendingResponseDTO getMyPendingList();
}
