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
    private String instCd;
    private String deptNm;
    private String email;

    @Builder
    public AuthorityListResponseDTO(Long authId, String userId, String hngNm, String userRole, String instCd, String deptNm, String email) {
        this.userId = userId;
        this.hngNm = hngNm;
        this.userRole = userRole;
        this.instCd = instCd;
        this.deptNm = deptNm;
        this.email = email;
    }
}
