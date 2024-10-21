package kr.or.kmi.mis.api.toner.model.request;

import kr.or.kmi.mis.api.toner.model.entity.TonerPrice;
import lombok.Getter;

@Getter
public class TonerPriceRequestDTO {

    private String modelNm;
    private String company;
    private String tonerNm;
    private String division;
    private String price;
    private String specialNote;

    public TonerPrice toEntity() {
        return TonerPrice.builder()
                .modelNm(modelNm)
                .company(company)
                .tonerNm(tonerNm)
                .division(division)
                .price(price)
                .specialNote(specialNote)
                .build();
    }
}
