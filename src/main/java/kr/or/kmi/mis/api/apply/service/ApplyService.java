package kr.or.kmi.mis.api.apply.service;

import kr.or.kmi.mis.api.apply.model.response.ApplyResponseDTO;
import kr.or.kmi.mis.api.apply.model.response.MyApplyResponseDTO;
import kr.or.kmi.mis.api.apply.model.response.PendingResponseDTO;

import java.time.LocalDate;

public interface ApplyService {

    ApplyResponseDTO getAllApplyList(String documentType, String instCd);
    MyApplyResponseDTO getAllMyApplyList(String userId);
    PendingResponseDTO getPendingListByType(String documentType, String instCd);
    PendingResponseDTO getMyPendingList(String userId);
}
