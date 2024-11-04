package kr.or.kmi.mis.api.toner.model.request;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TonerPriceDTO {
    private String tonerNm;  // 토너명
    private String price;    // 가격

    public static TonerPriceDTO of(String tonerNm, String price) {
        return TonerPriceDTO.builder()
                .tonerNm(tonerNm)
                .price(price)
                .build();
    }
}
