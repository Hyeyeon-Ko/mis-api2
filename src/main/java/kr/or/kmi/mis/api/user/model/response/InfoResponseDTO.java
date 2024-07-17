package kr.or.kmi.mis.api.user.model.response;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class InfoResponseDTO {

    String userId;
    String userName;

    public static InfoResponseDTO of(String userId, String userName) {
        return InfoResponseDTO.builder()
                .userId(userId)
                .userName(userName)
                .build();
    }
}
