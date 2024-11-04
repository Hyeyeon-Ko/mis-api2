package kr.or.kmi.mis.api.std.model.response;

import kr.or.kmi.mis.api.std.model.entity.StdClass;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class StdClassResponseDTO {
    private String classCd;
    private String classNm;

    public static StdClassResponseDTO of(StdClass stdClass) {
        return StdClassResponseDTO.builder()
                .classCd(stdClass.getClassCd())
                .classNm(stdClass.getClassNm())
                .build();
    }
}
