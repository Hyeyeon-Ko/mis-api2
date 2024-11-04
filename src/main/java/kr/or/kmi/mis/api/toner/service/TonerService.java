package kr.or.kmi.mis.api.toner.service;


import kr.or.kmi.mis.api.apply.model.request.ApplyRequestDTO;
import kr.or.kmi.mis.api.toner.model.request.TonerApplyRequestDTO;
import kr.or.kmi.mis.api.toner.model.request.TonerUpdateRequestDTO;
import kr.or.kmi.mis.api.toner.model.response.*;
import kr.or.kmi.mis.cmm.model.request.PostSearchRequestDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface TonerService {

    /* 토너 상세정보 호출 */
    TonerInfo2ResponseDTO getTonerInfo(String mngNum);
    /* 토너신청 상세정보 호출 */
    List<TonerApplyResponseDTO> getTonerApply(String draftId);
    /* 토너 신청 */
    void applyToner(TonerApplyRequestDTO tonerRequestDTO);
    /* 토너신청 수정 */
    void updateTonerApply(String draftId, TonerUpdateRequestDTO tonerUpdateRequestDTO);
    /* 토너신청 취소 */
    void cancelTonerApply(String draftId);
    /* 토너 관리번호 조회 */
    TonerMngResponseDTO getMngInfo(String instCd);

    /* 토너 나의 전체신청내역 */
    List<TonerMyResponseDTO> getMyTonerApply(ApplyRequestDTO applyRequestDTO, PostSearchRequestDTO postSearchRequestDTO);
    Page<TonerMyResponseDTO> getMyTonerApply2(ApplyRequestDTO applyRequestDTO, PostSearchRequestDTO postSearchRequestDTO, Pageable pageable);

    /* 토너 나의 승인대기내역 */
    List<TonerPendingListResponseDTO> getMyTonerPendingList(ApplyRequestDTO applyRequestDTO);

    /* 토너 전체신청내역 */
    Page<TonerMasterResponseDTO> getTonerApply2(ApplyRequestDTO applyRequestDTO, PostSearchRequestDTO postSearchRequestDTO, Pageable page);
}
