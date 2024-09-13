package kr.or.kmi.mis.api.noti.respository;

import kr.or.kmi.mis.api.noti.model.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
}
