package kr.or.kmi.mis.api.noti.respository;

import kr.or.kmi.mis.api.noti.model.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findAllByUserIdOrderByCreatedAtAsc(String userId);
}
