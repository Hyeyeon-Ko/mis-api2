package kr.or.kmi.mis.api.confirm.model.response;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class BcdHistoryResponseDTO {

    private String title;
    private String draftDate;
    private String applyStatus;
    private Integer quantity;

    @Builder
    public BcdHistoryResponseDTO(String title, String draftDate, String applyStatus, Integer quantity) {
        this.title = title;
        this.draftDate = draftDate;
        this.applyStatus = applyStatus;
        this.quantity = quantity;
    }
}
