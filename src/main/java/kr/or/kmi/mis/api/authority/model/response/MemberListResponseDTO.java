package kr.or.kmi.mis.api.authority.model.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class MemberListResponseDTO {

    private String userName; // 총무팀 이름
    private String userId; // 총무팀 사번
}
