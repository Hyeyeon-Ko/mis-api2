package kr.or.kmi.mis.api.noti.model.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@Getter
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notification_id")
    private Long id;

    private String content;

    private String url;

    private String receiver;

    private String type;

    private Boolean isRead;


    @Builder
    public Notification(String receiver, String type, String content, String url, Boolean isRead) {
        this.receiver = receiver;
        this.type = type;
        this.content = content;
        this.url = url;
        this.isRead = isRead;
    }



}
