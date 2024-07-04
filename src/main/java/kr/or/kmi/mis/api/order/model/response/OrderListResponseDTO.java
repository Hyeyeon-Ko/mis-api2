package kr.or.kmi.mis.api.order.model.response;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.sql.Timestamp;

@Getter
@RequiredArgsConstructor
public class OrderListResponseDTO {

    private String title;
    private Timestamp draftDate;
    private Timestamp respondDate;
    private String drafter;
    private Integer quantity;

    @Builder
    public OrderListResponseDTO(String title, Timestamp draftDate, Timestamp respondDate, String drafter, Integer quantity) {
        this.title = title;
        this.draftDate = draftDate;
        this.respondDate = respondDate;
        this.drafter = drafter;
        this.quantity = quantity;
    }
}
