package kr.or.kmi.mis.api.authority.model.response;

import kr.or.kmi.mis.api.authority.model.entity.Authority;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AuthorityResponseDTO {

    private String userId;
    private String userName;
    private String userRole;
    private String detailRole;

    public static AuthorityResponseDTO of(Authority authority, String detailRole) {
        return AuthorityResponseDTO.builder()
                .userId(authority.getUserId())
                .userName(authority.getHngNm())
                .userRole(authority.getRole())
                .detailRole(detailRole)
                .build();
    }
}
