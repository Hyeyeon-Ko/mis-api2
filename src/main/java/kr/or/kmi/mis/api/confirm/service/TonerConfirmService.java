package kr.or.kmi.mis.api.confirm.service;

import kr.or.kmi.mis.api.apply.model.request.ConfirmRequestDTO;

public interface TonerConfirmService {

    /* 승인 */
    void approve(String draftId, ConfirmRequestDTO confirmRequestDTO);

    /* 반려 */
    void disapprove(String draftId, ConfirmRequestDTO confirmRequestDTO);
}
