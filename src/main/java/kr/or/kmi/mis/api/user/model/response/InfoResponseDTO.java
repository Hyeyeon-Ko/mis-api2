package kr.or.kmi.mis.api.user.model.response;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class InfoResponseDTO {

    String currentUserId;
    String currentUserName;

    public static InfoResponseDTO of(String userId, String userName) {
        return InfoResponseDTO.builder()
                .currentUserId(userId)
                .currentUserName(userName)
                .build();
    }
}
