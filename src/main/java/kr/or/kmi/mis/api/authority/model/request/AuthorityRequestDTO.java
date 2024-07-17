package kr.or.kmi.mis.api.authority.model.request;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class AuthorityRequestDTO {
    String userId;
    String userNm;
    String userRole;
    String detailRole;

    @Builder
    public AuthorityRequestDTO(String userId, String userNm, String userRole, String detailRole) {
        this.userId = userId;
        this.userNm = userNm;
        this.userRole = userRole;
        this.detailRole = detailRole;
    }
}
