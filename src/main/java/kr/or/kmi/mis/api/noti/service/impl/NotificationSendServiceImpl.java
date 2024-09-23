package kr.or.kmi.mis.api.noti.service.impl;

import kr.or.kmi.mis.api.noti.model.entity.Notification;
import kr.or.kmi.mis.api.noti.model.response.SseResponseDTO;
import kr.or.kmi.mis.api.noti.respository.NotificationRepository;
import kr.or.kmi.mis.api.noti.service.NotificationSendService;
import kr.or.kmi.mis.api.noti.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationSendServiceImpl implements NotificationSendService {

    private final NotificationRepository notificationRepository;
    private final NotificationService notificationService;

    private static final SimpleDateFormat simpleDataFormat = new SimpleDateFormat("yyyy.MM.dd");

    @Override
    @Transactional
    public void sendBcdRejection(Timestamp draftDate, String drafterId) {
        String content = "[반려] " + simpleDataFormat.format(draftDate)
                + " [명함신청]이 반려되었습니다./반려 사유를 확인하세요.";

        Notification notification = this.createNotification(drafterId, content, "BCD");
        notificationRepository.save(notification);

        this.sendNotification(notification, drafterId, "명함신청 반려");
    }

    @Override
    @Transactional
    public void sendBcdOrder(Timestamp draftDate, String userId) {
        String content = "[수령확인] " + simpleDataFormat.format(draftDate)
                + " 신청한 명함이 [발주요청] 되었습니다./수령하신 후, 수령확인 버튼을 눌러주세요.";

        Notification notification = this.createNotification(userId, content, "BCD");
        notificationRepository.save(notification);

        this.sendNotification(notification, userId, "명함신청 수령안내");
    }

    @Override
    @Transactional
    public void sendCorpDocRejection(Timestamp draftDate, String drafterId) {
        String content = "[반려] " + simpleDataFormat.format(draftDate)
                + " [법인서류] 신청이 반려되었습니다./반려 사유를 확인하세요.";

       Notification notification = this.createNotification(drafterId, content, "CORPDOC");
       notificationRepository.save(notification);

       this.sendNotification(notification, drafterId, "법인서류신청 반려");
    }

    @Override
    @Transactional
    public void sendCorpDocApproval(Timestamp draftDate, String drafterId) {
        String content = "[승인완료] " + simpleDataFormat.format(draftDate)
                + " 신청한 [법인서류] 접수가 완료되었습니다./담당부서 방문 요청드립니다.";

        Notification notification = this.createNotification(drafterId, content, "CORPDOC");
        notificationRepository.save(notification);

        this.sendNotification(notification, drafterId, "법인서류신청 승인");
    }

    @Override
    @Transactional
    public void sendDocApproval(Timestamp draftDate, String drafterId, String division) {
        String docType = Objects.equals(division, "A") ? "수신문서" : "발신문서";

        String content = "[승인완료] " + simpleDataFormat.format(draftDate)
                + " 신청한 ["+ docType + "] 접수가 완료되었습니다./담당부서 방문 요청드립니다.";

        Notification notification = this.createNotification(drafterId, content, "DOC");
        notificationRepository.save(notification);

        this.sendNotification(notification, drafterId, "문서수발신 승인");
    }

    @Override
    @Transactional
    public void sendSealApproval(Timestamp draftDate, String drafterId) {

        String content = "[승인완료] " + simpleDataFormat.format(draftDate)
                + " 신청한 [날인요청] 접수가 완료되었습니다./담당부서 방문 요청드립니다.";

        Notification notification = this.createNotification(drafterId, content, "SEAL");
        notificationRepository.save(notification);

        this.sendNotification(notification, drafterId, "인장신청 승인");
    }

    @Override
    @Transactional
    public void sendSealDisapproval(Timestamp draftDate, String drafterId) {

        String content = "[반려] " + simpleDataFormat.format(draftDate)
                + " [인장신청]이 반려되었습니다./반려 사유를 확인하세요.";

        Notification notification = this.createNotification(drafterId, content, "SEAL");
        notificationRepository.save(notification);

        this.sendNotification(notification, drafterId, "인장신청 반려");
    }

    private Notification createNotification(String drafterId, String content, String type) {
        return Notification.builder()
                .userId(drafterId)
                .content(content)
                .type(type)
                .isRead(false)
                .build();
    }

    private void sendNotification(Notification notification, String drafterId, String comment) {
        SseResponseDTO sseResponseDTO = SseResponseDTO.of(notification);

        Long receiverId = Long.parseLong(drafterId);
        notificationService.customNotify(receiverId, sseResponseDTO, comment);
    };
}
