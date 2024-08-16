package kr.or.kmi.mis.api.bcd.model.request;

import lombok.Getter;

@Getter
public class BcdUpdateRequestDTO {

    String engNm;
    String instCd;
    String deptCd;
    String teamCd;
    String teamNm;
    String engTeamNm;
    String gradeCd;
    String gradeNm;
    String enGradeNm;
    String extTel;
    String phoneTel;
    String faxTel;
    String email;
    String address;
    String engAddress;
    String division;    // 명함구분
    Integer quantity;

}
