package kr.or.kmi.mis.api.apply.service;

import kr.or.kmi.mis.api.apply.model.request.ApplyRequestDTO;
import kr.or.kmi.mis.api.apply.model.response.*;
import kr.or.kmi.mis.cmm.model.request.PostSearchRequestDTO;
import org.springframework.data.domain.Pageable;

public interface ApplyService {

    ApplyListResponseDTO getAllApplyList(ApplyRequestDTO applyRequestDTO, PostSearchRequestDTO postSearchRequestDTO);
    ApplyResponseDTO getAllApplyList2(ApplyRequestDTO applyRequestDTO, PostSearchRequestDTO postSearchRequestDTO, Pageable pageable);
    MyApplyResponseDTO getAllMyApplyList2(ApplyRequestDTO applyRequestDTO, PostSearchRequestDTO postSearchRequestDTO, Pageable pageable);
    PendingResponseDTO getPendingListByType2(ApplyRequestDTO applyRequestDTO, PostSearchRequestDTO postSearchRequestDTO, Pageable pageable);
    PendingCountResponseDTO getPendingCountList(ApplyRequestDTO applyRequestDTO, PostSearchRequestDTO postSearchRequestDTO);
    PendingResponseDTO getMyPendingList2(ApplyRequestDTO applyRequestDTO, Pageable page);
}
