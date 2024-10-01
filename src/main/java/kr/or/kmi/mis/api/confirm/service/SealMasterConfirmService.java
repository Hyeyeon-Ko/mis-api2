package kr.or.kmi.mis.api.confirm.service;

public interface SealMasterConfirmService {

    /* 승인 */
    void approve(String draftId);

    /* 반려 */
    void disapprove(String draftId, String rejectReason);
}
