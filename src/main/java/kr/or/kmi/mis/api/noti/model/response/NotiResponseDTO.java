package kr.or.kmi.mis.api.noti.model.response;

import kr.or.kmi.mis.api.noti.model.entity.Notification;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.text.SimpleDateFormat;

@Data
@AllArgsConstructor
@Builder
public class NotiResponseDTO {

    private Long id;
    private String content;
    private String type;
    private Boolean isRead;
    private String createdDate;

    public static NotiResponseDTO of(Notification notification) {
        SimpleDateFormat simpleDateTimeFormat = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");

        return NotiResponseDTO.builder()
                .id(notification.getId())
                .content(notification.getContent())
                .type(notification.getType())
                .isRead(notification.getIsRead())
                .createdDate(simpleDateTimeFormat.format(notification.getCreatedAt()))
                .build();
    }
}
