package kr.or.kmi.mis.api.order.model.response;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.sql.Timestamp;

@Getter
@RequiredArgsConstructor
public class OrderListResponseDTO {

    private Long draftId;
    private String instNm;
    private String title;
    private Timestamp draftDate;
    private Timestamp respondDate;
    private String drafter;
    private Integer quantity;

    @Builder
    public OrderListResponseDTO(Long draftId, String instNm, String title, Timestamp draftDate, Timestamp respondDate, String drafter, Integer quantity) {
        this.draftId = draftId;
        this.instNm = instNm;
        this.title = title;
        this.draftDate = draftDate;
        this.respondDate = respondDate;
        this.drafter = drafter;
        this.quantity = quantity;
    }
}
