package kr.or.kmi.mis.api.confirm.service;

public interface DocConfirmService {
    void confirm(String draftId, String userId);
    void delete(String draftId);
}
