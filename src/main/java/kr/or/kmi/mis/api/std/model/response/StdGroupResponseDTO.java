package kr.or.kmi.mis.api.std.model.response;

import kr.or.kmi.mis.api.std.model.entity.StdGroup;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
@AllArgsConstructor
public class StdGroupResponseDTO {

    private String classCd;
    private String groupCd;
    private String groupNm;

    public static StdGroupResponseDTO of(StdGroup stdGroup) {
        return StdGroupResponseDTO.builder()
                .classCd(stdGroup.getClassCd().getClassCd())
                .groupCd(stdGroup.getGroupCd())
                .groupNm(stdGroup.getGroupNm())
                .build();
    }
}
