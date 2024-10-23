package kr.or.kmi.mis.api.toner.service;


import kr.or.kmi.mis.api.toner.model.request.TonerApplyRequestDTO;
import kr.or.kmi.mis.api.toner.model.response.TonerApplyResponseDTO;
import kr.or.kmi.mis.api.toner.model.response.TonerInfo2ResponseDTO;
import kr.or.kmi.mis.api.toner.model.response.TonerMngResponseDTO;

public interface TonerService {

    /* 토너 상세정보 호출 */
    TonerInfo2ResponseDTO getTonerInfo(String mngNum);
    /* 토너신청 상세정보 호출 */
    TonerApplyResponseDTO getTonerApply(String draftId);
    /* 토너 신청 */
    void applytoner(TonerApplyRequestDTO tonerRequestDTO);
    /* 토너신청 수정 */
    void updateTonerApply(TonerApplyRequestDTO tonerRequestDTO);
    /* 토너신청 취소 */
    void cancelTonerApply(String draftId);
    /* 토너 관리번호 조회 */
    TonerMngResponseDTO getMngInfo(String instCd);
}
