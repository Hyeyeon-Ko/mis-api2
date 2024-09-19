package kr.or.kmi.mis.api.confirm.service;

public interface DocConfirmService {
    void confirm(Long draftId, String userId);
    void delete(Long draftId);
}
