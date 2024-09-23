package kr.or.kmi.mis.api.noti.service;

import java.sql.Timestamp;

public interface NotificationSendService {

    void sendBcdRejection(Timestamp draftDate, String drafterId);
    void sendBcdOrder(Timestamp draftDate, String drafterId);
    void sendCorpDocRejection(Timestamp draftDate, String drafterId);
    void sendCorpDocApproval(Timestamp draftDate, String drafterId);
    void sendDocApproval(Timestamp draftDate, String drafterId, String division);
    void sendSealApproval(Timestamp draftDate, String drafterId);
    void sendSealDisapproval(Timestamp draftDate, String drafterId);
}
