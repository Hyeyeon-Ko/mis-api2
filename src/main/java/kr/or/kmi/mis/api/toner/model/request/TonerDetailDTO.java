package kr.or.kmi.mis.api.toner.model.request;

import lombok.Getter;

@Getter
public class TonerDetailDTO {

    private String mngNum;
    private String teamNm;
    private String location;
    private String printNm;
    private String tonerNm;
    private String price;
    private int quantity;
    private String totalPrice;

}
