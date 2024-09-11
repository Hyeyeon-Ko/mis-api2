package kr.or.kmi.mis.api.std.model.response;

import kr.or.kmi.mis.api.std.model.entity.StdDetail;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class StdResponseDTO {
    private String detailCd;
    private String detailNm;
    private String parentCd;
    private int level;
    private String teamCd;
    private String foundTeamCd;

    public static StdResponseDTO fromEntity(StdDetail stdDetail) {
        return StdResponseDTO.builder()
                .detailCd(stdDetail.getDetailCd())
                .detailNm(stdDetail.getDetailNm())
                .parentCd(stdDetail.getEtcItem1())
                .level(Integer.parseInt(stdDetail.getEtcItem3()))
                .teamCd(stdDetail.getEtcItem4())
                .build();
    }
}
