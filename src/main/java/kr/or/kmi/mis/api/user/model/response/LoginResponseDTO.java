package kr.or.kmi.mis.api.user.model.response;

import lombok.Data;

import java.util.List;

@Data
public class LoginResponseDTO {
    private String hngNm;
    private String role;
    private List<String> sidebarPermissions;
}
