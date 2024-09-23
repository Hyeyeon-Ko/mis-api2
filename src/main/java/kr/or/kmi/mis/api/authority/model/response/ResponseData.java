package kr.or.kmi.mis.api.authority.model.response;

import lombok.Data;

@Data
public class ResponseData {
    private String resultCd;
    private ResultData resultData;
    private String resultMsg;

    @Data
    public static class ResultData {
        private String usernm;
        private String userid;
        private String bzbzplceNm;
        private String bzbzplceCd;
        private String orgdeptnm;
        private String orgdeptcd;
        private String deptname;
        private String rolename;
        private String positionname;
        private String email;
        private String mpphonno;
    }
}
