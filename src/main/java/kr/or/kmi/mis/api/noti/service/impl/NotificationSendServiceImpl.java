package kr.or.kmi.mis.api.noti.service.impl;

import kr.or.kmi.mis.api.noti.model.entity.Notification;
import kr.or.kmi.mis.api.noti.model.response.NotiResponseDTO;
import kr.or.kmi.mis.api.noti.respository.NotificationRepository;
import kr.or.kmi.mis.api.noti.service.NotificationSendService;
import kr.or.kmi.mis.api.noti.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationSendServiceImpl implements NotificationSendService {

    private final NotificationRepository notificationRepository;
    private final NotificationService notificationService;

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Override
    @Transactional
    public void sendBcdRejection(LocalDateTime draftDate, String drafterId) {
        String content = "[반려] " + draftDate.format(formatter)
                + " [명함신청]이 반려되었습니다./반려 사유를 확인하세요.";

        Notification notification = this.createNotification(drafterId, content, "BCD");
        notificationRepository.save(notification);

        this.sendNotification(notification, drafterId, "명함신청 반려");
    }

    @Override
    @Transactional
    public void sendBcdOrder(LocalDateTime draftDate, String userId) {
        String content = "[수령확인] " + draftDate.format(formatter)
                + " 신청한 명함이 [발주요청] 되었습니다./수령하신 후, 수령확인 버튼을 눌러주세요.";

        Notification notification = this.createNotification(userId, content, "BCD");
        notificationRepository.save(notification);

        this.sendNotification(notification, userId, "명함신청 수령안내");
    }

    @Override
    @Transactional
    public void sendCorpDocRejection(LocalDateTime draftDate, String drafterId) {
        String content = "[반려] " + draftDate.format(formatter)
                + " [법인서류] 신청이 반려되었습니다./반려 사유를 확인하세요.";

        Notification notification = this.createNotification(drafterId, content, "CORPDOC");
        notificationRepository.save(notification);

        this.sendNotification(notification, drafterId, "법인서류신청 반려");
    }

    @Override
    @Transactional
    public void sendCorpDocApproval(LocalDateTime draftDate, String drafterId) {
        String content = "[승인완료] " + draftDate.format(formatter)
                + " 신청한 [법인서류] 접수가 완료되었습니다./담당부서 방문 요청드립니다.";

        Notification notification = this.createNotification(drafterId, content, "CORPDOC");
        notificationRepository.save(notification);

        this.sendNotification(notification, drafterId, "법인서류신청 승인");
    }

    @Override
    @Transactional
    public void sendDocApproval(LocalDateTime draftDate, String drafterId, String division) {
        String docType = Objects.equals(division, "A") ? "수신문서" : "발신문서";

        String content = "[승인완료] " + draftDate.format(formatter)
                + " 신청한 ["+ docType + "] 접수가 완료되었습니다./담당부서 방문 요청드립니다.";

        Notification notification = this.createNotification(drafterId, content, "DOC");
        notificationRepository.save(notification);

        this.sendNotification(notification, drafterId, "문서수발신 승인");
    }

    @Override
    @Transactional
    public void sendSealApproval(LocalDateTime draftDate, String drafterId) {

        String content = "[승인완료] " + draftDate.format(formatter)
                + " 신청한 [날인요청] 접수가 완료되었습니다./담당부서 방문 요청드립니다.";

        Notification notification = this.createNotification(drafterId, content, "SEAL");
        notificationRepository.save(notification);

        this.sendNotification(notification, drafterId, "인장신청 승인");
    }

    @Override
    @Transactional
    public void sendSealDisapproval(LocalDateTime draftDate, String drafterId) {

        String content = "[반려] " + draftDate.format(formatter)
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
        NotiResponseDTO notiResponseDTO = NotiResponseDTO.of(notification);

        Long receiverId = Long.parseLong(drafterId);
        notificationService.customNotify(receiverId, notiResponseDTO, comment);
    };
}
