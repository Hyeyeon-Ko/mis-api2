package kr.or.kmi.mis.api.confirm.service;

public interface SealMasterConfirmService {

    /* 승인 */
    void approve(Long draftId);

    /* 반려 */
    void disapprove(Long draftId, String rejectReason);
}
