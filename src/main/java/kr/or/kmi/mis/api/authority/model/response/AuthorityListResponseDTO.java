package kr.or.kmi.mis.api.authority.model.response;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class AuthorityListResponseDTO {
    private Long authId;
    private String userId;
    private String hngNm;
    private String userRole;
    private String instNm;
    private String deptNm;
    private String email;
    private String detailCd;

    @Builder
    public AuthorityListResponseDTO(Long authId, String userId, String hngNm, String userRole, String instNm, String deptNm, String email, String detailCd) {
        this.authId = authId;
        this.userId = userId;
        this.hngNm = hngNm;
        this.userRole = userRole;
        this.instNm = instNm;
        this.deptNm = deptNm;
        this.email = email;
        this.detailCd = detailCd;
    }
}
