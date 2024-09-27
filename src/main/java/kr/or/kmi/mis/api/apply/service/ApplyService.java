package kr.or.kmi.mis.api.apply.service;

import kr.or.kmi.mis.api.apply.model.response.ApplyResponseDTO;
import kr.or.kmi.mis.api.apply.model.response.MyApplyResponseDTO;
import kr.or.kmi.mis.api.apply.model.response.PendingCountResponseDTO;
import kr.or.kmi.mis.api.apply.model.response.PendingResponseDTO;

import java.time.LocalDate;

public interface ApplyService {

    ApplyResponseDTO getAllApplyList(String documentType, LocalDate startDate, LocalDate endDate, String searchType, String keyword, String instCd, String userId);
    MyApplyResponseDTO getAllMyApplyList(String documentType, LocalDate startDate, LocalDate endDate, String userId);
    PendingResponseDTO getPendingListByType(String documentType, LocalDate startDate, LocalDate endDate, String instCd, String userId);
    PendingCountResponseDTO getPendingCountList(String documentType, LocalDate startDate, LocalDate endDate, String instCd, String userId);
    PendingResponseDTO getMyPendingList(String userId);
}
