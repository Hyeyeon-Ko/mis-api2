package kr.or.kmi.mis.api.std.model.response.bcd;

import kr.or.kmi.mis.api.std.model.entity.StdDetail;
import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.stream.Collectors;

@Builder
@Data
public class StdStatusResponseDTO {

    private String statusCd;
    private String statusNm;

    public static List<StdStatusResponseDTO> of(List<StdDetail> stdDetails) {
        return stdDetails.stream()
                .map(stdDetail -> StdStatusResponseDTO.builder()
                        .statusCd(stdDetail.getDetailCd())
                        .statusNm(stdDetail.getDetailNm())
                        .build())
                .collect(Collectors.toList());
    }
}
