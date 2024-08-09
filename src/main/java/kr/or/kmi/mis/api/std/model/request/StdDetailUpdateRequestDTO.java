package kr.or.kmi.mis.api.std.model.request;

import kr.or.kmi.mis.api.std.model.entity.StdDetail;
import kr.or.kmi.mis.api.std.model.entity.StdGroup;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
@AllArgsConstructor
public class StdDetailUpdateRequestDTO {

    private String groupCd;
    private String detailCd;
    private String detailNm;
    private String etcItem1;
    private String etcItem2;
    private String etcItem3;
    private String etcItem4;
    private String etcItem5;
    private String etcItem6;
    private String etcItem7;
    private String etcItem8;

}
