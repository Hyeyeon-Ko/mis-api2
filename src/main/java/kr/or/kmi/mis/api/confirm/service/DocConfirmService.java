package kr.or.kmi.mis.api.confirm.service;

public interface DocConfirmService {
    void confirm(Long draftId);
    void delete(Long draftId);
    void revert(Long draftId);
}
