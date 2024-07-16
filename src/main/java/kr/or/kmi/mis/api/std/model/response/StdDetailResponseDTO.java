package kr.or.kmi.mis.api.std.model.response;

import kr.or.kmi.mis.api.std.model.entity.StdDetail;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
@AllArgsConstructor
public class StdDetailResponseDTO {

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

    public static StdDetailResponseDTO of(StdDetail stdDetail) {

        return StdDetailResponseDTO.builder()
                .detailCd(stdDetail.getDetailCd())
                .groupCd(stdDetail.getGroupCd().getGroupCd())
                .detailNm(stdDetail.getDetailNm())
                .fromDd(stdDetail.getFromDd())
                .toDd(stdDetail.getToDd())
                .etcItem1(stdDetail.getEtcItem1())
                .etcItem2(stdDetail.getEtcItem2())
                .etcItem3(stdDetail.getEtcItem3())
                .etcItem4(stdDetail.getEtcItem4())
                .etcItem5(stdDetail.getEtcItem5())
                .build();
    }
}
