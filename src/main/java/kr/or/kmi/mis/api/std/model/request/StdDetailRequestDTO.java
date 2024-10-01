package kr.or.kmi.mis.api.std.model.request;

import kr.or.kmi.mis.api.std.model.entity.StdDetail;
import kr.or.kmi.mis.api.std.model.entity.StdGroup;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Builder
@Data
@AllArgsConstructor
public class StdDetailRequestDTO {

    private String detailCd;
    private String groupCd;
    private String detailNm;
    private String etcItem1;
    private String etcItem2;
    private String etcItem3;
    private String etcItem4;
    private String etcItem5;
    private String etcItem6;
    private String etcItem7;
    private String etcItem8;
    private String etcItem9;
    private String etcItem10;
    private String etcItem11;

    public StdDetail toEntity(StdGroup groupCd) {
        return StdDetail.builder()
                .detailCd(detailCd)
                .groupCd(groupCd)
                .detailNm(detailNm)
                .etcItem1(etcItem1)
                .etcItem2(etcItem2)
                .etcItem3(etcItem3)
                .etcItem4(etcItem4)
                .etcItem5(etcItem5)
                .etcItem6(etcItem6)
                .etcItem7(etcItem7)
                .etcItem8(etcItem8)
                .etcItem9(etcItem9)
                .etcItem10(etcItem10)
                .etcItem11(etcItem11)
                .build();
    }
}
