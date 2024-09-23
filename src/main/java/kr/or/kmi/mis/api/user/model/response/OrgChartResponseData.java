package kr.or.kmi.mis.api.user.model.response;

import lombok.Data;
import java.util.List;

@Data
public class OrgChartResponseData {
    private String resultCd;
    private List<OrgChartData> resultData;

    @Data
    public static class OrgChartData {
        private String positionname;
        private String rolename;
        private String deptcode;
        private String userid;
        private String username;
    }
}
