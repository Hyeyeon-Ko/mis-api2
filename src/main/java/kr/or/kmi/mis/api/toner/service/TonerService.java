package kr.or.kmi.mis.api.toner.service;


import kr.or.kmi.mis.api.toner.model.request.TonerRequestDTO;
import kr.or.kmi.mis.api.toner.model.response.TonerApplyResponseDTO;
import kr.or.kmi.mis.api.toner.model.response.TonerInfo2ResponseDTO;

public interface TonerService {

    /* 토너 상세정보 호출 */
    TonerInfo2ResponseDTO getTonerInfo(String mngNum);
    /* 토너신청 상세정보 호출 */
    TonerApplyResponseDTO getTonerApply(Long draftId);
    /* 토너 신청 */
    void applytoner(TonerRequestDTO tonerRequestDTO);
    /* 토너신청 수정 */
    void updateTonerApply(TonerRequestDTO tonerRequestDTO);
    /* 토너신청 취소 */
    void cancelTonerApply(Long draftId);
}
