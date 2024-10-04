package kr.or.kmi.mis.api.apply.service;

import kr.or.kmi.mis.api.apply.model.request.ApplyRequestDTO;
import kr.or.kmi.mis.api.apply.model.response.ApplyResponseDTO;
import kr.or.kmi.mis.api.apply.model.response.MyApplyResponseDTO;
import kr.or.kmi.mis.api.apply.model.response.PendingCountResponseDTO;
import kr.or.kmi.mis.api.apply.model.response.PendingResponseDTO;
import kr.or.kmi.mis.cmm.model.request.PostSearchRequestDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;

public interface ApplyService {

    ApplyResponseDTO getAllApplyList(String documentType, LocalDateTime startDate, LocalDateTime endDate, String searchType, String keyword, String instCd, String userId);
    ApplyResponseDTO getAllApplyList2(ApplyRequestDTO applyRequestDTO, PostSearchRequestDTO postSearchRequestDTO, Pageable pageable);
    MyApplyResponseDTO getAllMyApplyList(String documentType, LocalDateTime startDate, LocalDateTime endDate, String userId);
    MyApplyResponseDTO getAllMyApplyList2(ApplyRequestDTO applyRequestDTO, PostSearchRequestDTO postSearchRequestDTO, Pageable pageable);
    PendingResponseDTO getPendingListByType(String documentType, LocalDateTime startDate, LocalDateTime endDate, String instCd, String userId);
    PendingCountResponseDTO getPendingCountList(String documentType, LocalDateTime startDate, LocalDateTime endDate, String instCd, String userId);
    PendingResponseDTO getMyPendingList(String userId);
}
