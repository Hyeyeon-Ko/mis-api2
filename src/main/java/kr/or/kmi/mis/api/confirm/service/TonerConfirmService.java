package kr.or.kmi.mis.api.confirm.service;

import kr.or.kmi.mis.api.toner.model.request.TonerConfirmRequestDTO;

public interface TonerConfirmService {

    /* 승인 */
    void approve(TonerConfirmRequestDTO tonerConfirmRequestDTO);

    /* 반려 */
    void disapprove(TonerConfirmRequestDTO tonerConfirmRequestDTO);
}
