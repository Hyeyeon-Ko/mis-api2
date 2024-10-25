package kr.or.kmi.mis.api.user.model.request;

import lombok.Data;

@Data
public class SessionInfoRequestDTO {
    private String userId;
    private String userNm;
}
