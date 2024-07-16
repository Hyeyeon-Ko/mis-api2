package kr.or.kmi.mis.api.std.model.request;

import kr.or.kmi.mis.api.std.model.entity.StdDetail;
import kr.or.kmi.mis.api.std.model.entity.StdGroup;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.sql.Timestamp;

@Builder
@Data
@AllArgsConstructor
public class StdDetailRequestDTO {

    private String detailCd;
    private String groupCd;
    private String detailNm;
    private String fromDd;
    private String toDd;
    private String etcItem1;
    private String etcItem2;
    private String etcItem3;
    private String etcItem4;
    private String etcItem5;
    private String etcItem6;

    public StdDetail toEntity(StdGroup groupCd) {
        return StdDetail.builder()
                .detailCd(detailCd)
                .groupCd(groupCd)
                .detailNm(detailNm)
                .fromDd(fromDd)
                .toDd(toDd)
                .etcItem1(etcItem1)
                .etcItem2(etcItem2)
                .etcItem3(etcItem3)
                .etcItem4(etcItem4)
                .etcItem5(etcItem5)
                .etcItem6(etcItem6)
                .build();
    }
}
