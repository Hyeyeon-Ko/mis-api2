package kr.or.kmi.mis.api.toner.model.response;

import kr.or.kmi.mis.api.toner.model.entity.TonerPrice;
import lombok.Builder;
import lombok.Data;

@Data
public class TonerPriceResponseDTO {

    private String company;
    private String modelNm;
    private String tonerNm;
    private String division;
    private String price;
    private String specialNote;

    @Builder
    public TonerPriceResponseDTO(String company, String modelNm, String tonerNm, String division, String price, String specialNote) {
        this.company = company;
        this.modelNm = modelNm;
        this.tonerNm = tonerNm;
        this.division = division;
        this.price = price;
        this.specialNote = specialNote;
    }

    public static TonerPriceResponseDTO of(TonerPrice tonerPrice) {
        return TonerPriceResponseDTO.builder()
                .company(tonerPrice.getCompany())
                .modelNm(tonerPrice.getModelNm())
                .tonerNm(tonerPrice.getTonerNm())
                .division(tonerPrice.getDivision())
                .price(tonerPrice.getPrice())
                .specialNote(tonerPrice.getSpecialNote())
                .build();
    }

}
