package kr.or.kmi.mis.api.authority.model.response;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class AuthorityListResponseDTO {

    private String userId;
    private String hngNm;
    private String instCd;
    private String deptCd;
    private String deptNm;
    private String email;

    @Builder
    public AuthorityListResponseDTO(String userId, String hngNm, String instCd, String deptCd, String deptNm, String email) {
        this.userId = userId;
        this.hngNm = hngNm;
        this.instCd = instCd;
        this.deptCd = deptCd;
        this.deptNm = deptNm;
        this.email = email;
    }
}
