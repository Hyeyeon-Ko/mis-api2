package kr.or.kmi.mis.api.noti.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.or.kmi.mis.api.exception.EntityNotFoundException;
import kr.or.kmi.mis.api.noti.model.entity.Notification;
import kr.or.kmi.mis.api.noti.model.response.NotiResponseDTO;
import kr.or.kmi.mis.api.noti.respository.EmitterRepository;
import kr.or.kmi.mis.api.noti.respository.NotificationRepository;
import kr.or.kmi.mis.api.noti.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final EmitterRepository emitterRepository;

    @Override
    @Transactional
    public <T> void customNotify(Long userId, T data, String comment) {
        log.info("Custom notify user {}", userId);
        sendToClient(userId, data, comment);
    }

    private <T> void sendToClient(Long userId, T data, String comment) {
        log.info("sendToClient 진입 >>>> Custom notify user {}", userId);
        SseEmitter emitter = emitterRepository.get(userId);

        if (emitter != null) {
            log.info("emitter : {}", emitter);
            try {
                String jsonData = new ObjectMapper().writeValueAsString(data);

                emitter.send(SseEmitter.event()
                        .id(String.valueOf(userId))
                        .name("notification")
                        .data(jsonData, MediaType.APPLICATION_JSON)
                        .comment(comment));
            } catch (IOException e) {
                emitterRepository.deleteById(userId);
                emitter.completeWithError(e);
            }
        } else {
            log.warn("No emitter found for user {}", userId);
        }
    }

    // 알림 읽음 처리
    @Override
    @Transactional
    public void markAsRead(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new EntityNotFoundException("Notification not found"));
        notification.markAsRead();
        notificationRepository.save(notification);
    }

    // 모든 알림 읽음 처리
    @Override
    @Transactional
    public void markAllAsRead(String userId) {
        List<Notification> notifications = notificationRepository.findAllByIsReadAndUserId(false, userId);
        notifications.stream()
                .peek(Notification::markAsRead)
                .forEach(notificationRepository::save);
    }

    @Override
    @Transactional(readOnly = true)
    public List<NotiResponseDTO> getAllNotification(String userId) {
        List<Notification> notifications = notificationRepository.findAllByUserIdOrderByCreatedAtAsc(userId);

        return notifications.stream()
                .map(NotiResponseDTO::of).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public int getUnreadNotificationNum(String userId) {
        return notificationRepository.countByUserIdAndIsRead(userId, false);
    }

}
