package kr.or.kmi.mis.api.authority.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AuthorityResponseDTO2 {
    private Long authId;
    private String userId;
    private String hngNm;
    private String userRole;
    private String instNm;
    private String deptNm;
    private String email;

    @Builder
    public static AuthorityResponseDTO2 of(Long authId, String userId, String hngNm, String userRole, String instNm, String deptNm, String email) {
        return AuthorityResponseDTO2.builder()
                .authId(authId)
                .userId(userId)
                .hngNm(hngNm)
                .userRole(userRole)
                .instNm(instNm)
                .deptNm(deptNm)
                .email(email)
                .build();
    }
}
