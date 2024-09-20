package kr.or.kmi.mis.api.noti.model.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.sql.Timestamp;

@Entity
@NoArgsConstructor
@Getter
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notification_id")
    private Long id;

    private String userId;

    private String content;

    private String type;

    private Boolean isRead;

    @CreationTimestamp
    private Timestamp createdAt;

    @Builder
    public Notification(String userId, String content, String type, Boolean isRead) {
        this.userId = userId;
        this.content = content;
        this.type = type;
        this.isRead = isRead;
    }

    public void markAsRead() {
        this.isRead = true;
    }
}
