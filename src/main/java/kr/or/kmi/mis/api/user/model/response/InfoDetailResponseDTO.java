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

    public static InfoDetailResponseDTO of(String userId, String userName, String telNum, String email) {
        return InfoDetailResponseDTO.builder()
                .userId(userId)
                .userName(userName)
                .telNum(telNum)
                .email(email)
                .build();
    }
}
