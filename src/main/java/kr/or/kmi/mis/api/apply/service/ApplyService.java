package kr.or.kmi.mis.api.apply.service;

import kr.or.kmi.mis.api.apply.model.response.ApplyResponseDTO;
import kr.or.kmi.mis.api.apply.model.response.MyApplyResponseDTO;
import kr.or.kmi.mis.api.apply.model.response.PendingResponseDTO;

import java.time.LocalDate;

public interface ApplyService {

    ApplyResponseDTO getAllApplyList(String documentType, LocalDate startDate, LocalDate endDate);
    MyApplyResponseDTO getAllMyApplyList(String documentType, LocalDate startDate, LocalDate endDate);
    PendingResponseDTO getAllPendingList();
    PendingResponseDTO getMyPendingList();
}
