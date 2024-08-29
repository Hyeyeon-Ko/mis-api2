package kr.or.kmi.mis.api.confirm.service;

public interface CorpDocConfirmService {
    /** 법인서류 신청 승인 */
    void approve(Long draftId);
    /** 법인서류 신청 반려 */
    void reject(Long draftId, String rejectReason);
}
