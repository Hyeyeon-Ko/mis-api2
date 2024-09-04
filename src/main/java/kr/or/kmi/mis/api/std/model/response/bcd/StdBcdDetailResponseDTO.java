package kr.or.kmi.mis.api.std.model.response.bcd;

import kr.or.kmi.mis.api.std.model.entity.StdDetail;
import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.stream.Collectors;

@Builder
@Data
public class StdBcdDetailResponseDTO {

    String detailCd;
    String detailNm;
    String etcItem1;
    String etcItem2;
    String etcItem3;
    String etcItem4;
    String etcItem5;
    String etcItem6;
    String etcItem7;
    String etcItem8;
    String etcItem9;
    String etcItem10;
    String etcItem11;

    public static List<StdBcdDetailResponseDTO> of(List<StdDetail> stdDetails) {
        return stdDetails.stream()
                .map(stdDetail -> StdBcdDetailResponseDTO.builder()
                        .detailCd(stdDetail.getDetailCd())
                        .detailNm(stdDetail.getDetailNm())
                        .etcItem1(stdDetail.getEtcItem1())
                        .etcItem2(stdDetail.getEtcItem2())
                        .etcItem3(stdDetail.getEtcItem3())
                        .etcItem4(stdDetail.getEtcItem4())
                        .etcItem5(stdDetail.getEtcItem5())
                        .etcItem6(stdDetail.getEtcItem6())
                        .etcItem7(stdDetail.getEtcItem7())
                        .etcItem8(stdDetail.getEtcItem8())
                        .etcItem9(stdDetail.getEtcItem9())
                        .etcItem10(stdDetail.getEtcItem10())
                        .etcItem11(stdDetail.getEtcItem11())
                        .build())
                .collect(Collectors.toList());
    }
}
