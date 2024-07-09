package kr.or.kmi.mis.api.user.model.request;

import lombok.Data;

@Data
public class LoginRequestDTO {

    private String userId;
    private String userPw;
}
