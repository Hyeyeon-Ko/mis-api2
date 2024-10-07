package kr.or.kmi.mis.api.confirm.service;

import kr.or.kmi.mis.api.apply.model.request.ConfirmRequestDTO;

public interface CorpDocConfirmService {
    /** 법인서류 신청 승인 */
    void approve(String draftId, ConfirmRequestDTO confirmRequestDTO);
    /** 법인서류 신청 반려 */
    void reject(String draftId, ConfirmRequestDTO confirmRequestDTO);
}
