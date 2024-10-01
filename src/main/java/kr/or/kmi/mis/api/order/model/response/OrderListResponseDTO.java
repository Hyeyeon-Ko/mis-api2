package kr.or.kmi.mis.api.order.model.response;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Getter
@RequiredArgsConstructor
public class OrderListResponseDTO {

    private String draftId;
    private String instNm;
    private String title;
    private LocalDateTime draftDate;
    private LocalDateTime respondDate;
    private String drafter;
    private Integer quantity;

    @Builder
    public OrderListResponseDTO(String draftId, String instNm, String title, LocalDateTime draftDate, LocalDateTime respondDate, String drafter, Integer quantity) {
        this.draftId = draftId;
        this.instNm = instNm;
        this.title = title;
        this.draftDate = draftDate;
        this.respondDate = respondDate;
        this.drafter = drafter;
        this.quantity = quantity;
    }
}
