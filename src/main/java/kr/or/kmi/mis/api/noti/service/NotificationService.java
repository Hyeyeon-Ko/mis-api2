package kr.or.kmi.mis.api.noti.service;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

public interface NotificationService {
    SseEmitter subscribe(String userId);
    <T> void customNotify(Long userId, T data, String comment);
    void markAsRead(Long notificationId);
}
