package kr.or.kmi.mis.api.user.model.response;

import lombok.Data;

import java.util.List;

@Data
public class LoginResponseDTO {
    private String userNm;
    private String role;
    private String instCd;
    private String deptCd;
    private String teamCd;
    private String roleNm;
    private List<String> sidebarPermissions;
}
