package kr.or.kmi.mis.api.confirm.model.response;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Getter
@RequiredArgsConstructor
public class BcdHistoryResponseDTO {

    private String title;
    private LocalDateTime draftDate;
    private String applyStatus;
    private Integer quantity;

    @Builder
    public BcdHistoryResponseDTO(String title, LocalDateTime draftDate, String applyStatus, Integer quantity) {
        this.title = title;
        this.draftDate = draftDate;
        this.applyStatus = applyStatus;
        this.quantity = quantity;
    }
}
