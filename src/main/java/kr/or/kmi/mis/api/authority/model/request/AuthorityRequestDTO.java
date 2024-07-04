package kr.or.kmi.mis.api.authority.model.request;

import kr.or.kmi.mis.api.authority.model.entity.Authority;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.sql.Timestamp;

@Getter
@RequiredArgsConstructor
public class AuthorityRequestDTO {

    private String userId;
    private String hngNm;
    private String instCd;
    private String deptCd;
    private String deptNm;
    private String email;
    private String role;
    private Timestamp createdt;

    // AuthorityRequest -> Authority Entity
    public Authority toAuthorityEntity() {
        return Authority.builder()
                .userId(userId)
                .hngNm(hngNm)
                .instCd(instCd)
                .deptCd(deptCd)
                .deptNm(deptNm)
                .email(email)
                .role("B")
                .createdt(new Timestamp(System.currentTimeMillis()))
                .build();
    }
}
