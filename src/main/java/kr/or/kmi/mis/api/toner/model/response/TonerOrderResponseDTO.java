package kr.or.kmi.mis.api.toner.model.response;

import lombok.Builder;
import lombok.Getter;

@Getter
public class TonerOrderResponseDTO {

    private String tonerNm;
    private int quantity;
    private int price;
    private String totalPrice;
    private String mngNum;

    @Builder
    public TonerOrderResponseDTO(String tonerNm, int quantity, int price, String totalPrice, String mngNum) {
        this.tonerNm = tonerNm;
        this.quantity = quantity;
        this.price = price;
        this.totalPrice = totalPrice;
        this.mngNum = mngNum;
    }
}
