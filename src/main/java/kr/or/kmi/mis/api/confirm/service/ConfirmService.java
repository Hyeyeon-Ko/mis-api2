package kr.or.kmi.mis.api.confirm.service;

import kr.or.kmi.mis.api.bcd.model.response.BcdDetailResponseDTO;

public interface ConfirmService {

    /* 신청 상세 정보 불러오기 */
    BcdDetailResponseDTO getBcdDetailInfo(Long id);

    /* 승인 */
    void approve(Long id);

    /* 반려 */
    void disapprove(Long id, String rejectReason);
}
