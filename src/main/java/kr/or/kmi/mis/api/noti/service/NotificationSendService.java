package kr.or.kmi.mis.api.noti.service;

import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;

public interface NotificationSendService {

    @Transactional
    void sendBcdRejection(Timestamp draftDate, String drafterId);

    @Transactional
    void sendBcdOrder(Timestamp draftDate, String drafterId);

    void sendCorpDocRejection(Timestamp draftDate, String drafterId);
    void sendCorpDocApproval(Timestamp draftDate, String drafterId);
    void sendDocApproval(Timestamp draftDate, String drafterId, String divsion);

    @Transactional
    void sendSealApproval(Timestamp draftDate, String drafterId);

    @Transactional
    void sendSealDisapproval(Timestamp draftDate, String drafterId);
}
