package kr.or.kmi.mis.api.user.model.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ConfirmResponseDTO {

    private String teamLeaderId;
    private String teamLeaderNm;
    private String teamLeaderRoleNm;
    private String teamLeaderPositionNm;
    private String teamLeaderDept;
    private String managerId;
    private String managerNm;
    private String managerRoleNm;
    private String managerPositionNm;
    private String managerDept;
}
