package kr.or.kmi.mis.api.noti.service;

import java.time.LocalDateTime;

public interface NotificationSendService {

    void sendBcdRejection(LocalDateTime draftDate, String drafterId);
    void sendBcdOrder(LocalDateTime draftDate, String drafterId);
    void sendCorpDocRejection(LocalDateTime draftDate, String drafterId);
    void sendCorpDocApproval(LocalDateTime draftDate, String drafterId);
    void sendDocApproval(LocalDateTime draftDate, String drafterId, String division);
    void sendSealApproval(LocalDateTime draftDate, String drafterId);
    void sendSealDisapproval(LocalDateTime draftDate, String drafterId);
    void sendTonerApproval(LocalDateTime draftDate, String drafterId);
    void sendTonerRejection(LocalDateTime draftDate, String drafterId);
    void sendTonerOrder(LocalDateTime draftDate, String drafterId);
}
