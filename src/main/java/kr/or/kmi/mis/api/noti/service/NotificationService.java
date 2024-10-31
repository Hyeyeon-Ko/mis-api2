package kr.or.kmi.mis.api.noti.service;

import kr.or.kmi.mis.api.noti.model.response.NotiResponseDTO;

import java.util.List;

public interface NotificationService {
    <T> void customNotify(Long userId, T data, String comment);
    void markAsRead(Long notificationId);

    void markAllAsRead(String userId);
    List<NotiResponseDTO> getAllNotification(String userId);
    int getUnreadNotificationNum(String userId);
}
