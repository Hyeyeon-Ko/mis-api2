package kr.or.kmi.mis.api.apply.model.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ApprovalLineResponseDTO {

    private String userId;
    private String userName;
    private String roleNm;
    private String positionNm;
    private int currentApproverIndex;
}
