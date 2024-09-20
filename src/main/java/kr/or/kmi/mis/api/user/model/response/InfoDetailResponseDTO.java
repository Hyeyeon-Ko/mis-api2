package kr.or.kmi.mis.api.user.model.response;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class InfoDetailResponseDTO {

    String userId;
    String userName;
    String telNum;
    String email;
    String instCd;
    String instNm;
    String teamCd;
    String deptNm;
    String roleNm;
    String positionNm;

    public static InfoDetailResponseDTO of(String userId, String userName, String telNum, String email,
                                           String instCd, String instNm, String teamCd, String deptNm, String roleNm, String positionNm) {
        return InfoDetailResponseDTO.builder()
                .userId(userId)
                .userName(userName)
                .telNum(telNum)
                .email(email)
                .instCd(instCd)
                .instNm(instNm)
                .teamCd(teamCd)
                .deptNm(deptNm)
                .roleNm(roleNm)
                .positionNm(positionNm)
                .build();
    }
}
