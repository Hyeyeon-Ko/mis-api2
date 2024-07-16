package kr.or.kmi.mis.api.bcd.model.request;

import lombok.Getter;

@Getter
public class BcdUpdateRequestDTO {

    String engNm;
    String instCd;
    String deptCd;
    String teamCd;
    String teamNm;
    String gradeCd;
    String extTel;
    String phoneTel;
    String faxTel;
    String email;
    String address;
    String engAddress;
    String division;    // 명함구분
    Integer quantity;

}
