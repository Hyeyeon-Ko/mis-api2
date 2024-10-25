package kr.or.kmi.mis.api.noti.service;

import java.time.LocalDateTime;
import java.util.List;

public interface NotificationSendService {

    void sendBcdRejection(LocalDateTime draftDate, String drafterId);
    void sendBcdOrder(LocalDateTime draftDate, String drafterId);
    void sendBcdReceipt(List<String> draftIds);
    void sendCorpDocRejection(LocalDateTime draftDate, String drafterId);
    void sendCorpDocApproval(LocalDateTime draftDate, String drafterId);
    void sendDocApproval(LocalDateTime draftDate, String drafterId, String division);
    void sendSealApproval(LocalDateTime draftDate, String drafterId);
    void sendSealDisapproval(LocalDateTime draftDate, String drafterId);
    void sendTonerApproval(LocalDateTime draftDate, String drafterId);
    void sendTonerRejection(LocalDateTime draftDate, String drafterId);
    void sendTonerOrder(LocalDateTime draftDate, String drafterId);
}
