package kr.or.kmi.mis.api.noti.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.sql.Timestamp;

@Data
@AllArgsConstructor
@Builder
public class SseResponseDTO {

    private Long draftId;
    private String content;
    private String type;
    private String respondDate;

    public static SseResponseDTO of(Long draftId, String content, String type, String respondDate) {
        return SseResponseDTO.builder()
                .draftId(draftId)
                .content(content)
                .type(type)
                .respondDate(respondDate)
                .build();
    }
}
