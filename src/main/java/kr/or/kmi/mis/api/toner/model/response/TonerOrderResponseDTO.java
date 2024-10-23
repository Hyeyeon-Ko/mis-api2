package kr.or.kmi.mis.api.toner.model.response;

import lombok.Builder;
import lombok.Getter;

@Getter
public class TonerOrderResponseDTO {

    private String draftId;
    private String tonerNm;
    private int quantity;
    private int price;
    private String totalPrice;
    private String mngNum;
    private String holding;

    @Builder
    public TonerOrderResponseDTO(String draftId, String tonerNm, int quantity, int price,
                                 String totalPrice, String mngNum, String holding) {
        this.draftId = draftId;
        this.tonerNm = tonerNm;
        this.quantity = quantity;
        this.price = price;
        this.totalPrice = totalPrice;
        this.mngNum = mngNum;
        this.holding = holding;
    }
}
