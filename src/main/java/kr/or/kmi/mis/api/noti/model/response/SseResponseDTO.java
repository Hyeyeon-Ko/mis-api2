package kr.or.kmi.mis.api.noti.model.response;

import kr.or.kmi.mis.api.noti.model.entity.Notification;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class SseResponseDTO {

    private Long id;
    private String content;
    private String type;
    private Boolean isRead;
    private String respondDate;

    public static SseResponseDTO of(Notification notification, String respondDate) {
        return SseResponseDTO.builder()
                .id(notification.getId())
                .content(notification.getContent())
                .type(notification.getType())
                .isRead(notification.getIsRead())
                .respondDate(respondDate)
                .build();
    }
}
