package kr.or.kmi.mis.api.bcd.model.response;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class BcdDetailResponseDTO {

    private String drafter;
    private String userId;
    private String korNm;
    private String engNm;
    private String instNm;
    private String deptNm;
    private String teamNm;
    private String grade;
    private String extTel;
    private String faxTel;
    private String phoneTel;
    private String email;
    private String address;
    private String division;

    @Builder
    public BcdDetailResponseDTO(String drafter, String userId, String korNm, String engNm, String instNm, String deptNm, String teamNm, String grade,
                                String extTel, String faxTel, String phoneTel, String email, String address, String division) {
        this.drafter = drafter;
        this.userId = userId;
        this.korNm = korNm;
        this.engNm = engNm;
        this.instNm = instNm;
        this.deptNm = deptNm;
        this.teamNm = teamNm;
        this.grade = grade;
        this.extTel = extTel;
        this.faxTel = faxTel;
        this.phoneTel = phoneTel;
        this.email = email;
        this.address = address;
        this.division = division;
    }

}
