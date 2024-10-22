package kr.or.kmi.mis.api.toner.model.request;

import lombok.Getter;

@Getter
public class TonerDetailDTO {

    private String mngNum;
    private String teamNm;
    private String location;
    private String printNm;
    private String tonerNm;
//    private String color; // N(없음),B(흑백),L(컬러),K(검정),C(파랑),M(빨강),Y(노랑)
    private int quantity;
    private String totalPrice;

}
