package kr.or.kmi.mis.api.user.model.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OrgChartResponseDTO {

    private String userId;
    private String userNm;
    private String deptCd;
    private String positionNm;
}
