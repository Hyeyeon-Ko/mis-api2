package kr.or.kmi.mis.api.toner.model.response;

import kr.or.kmi.mis.api.toner.model.entity.TonerInfo;
import kr.or.kmi.mis.api.toner.model.request.TonerPriceDTO;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class TonerInfo2ResponseDTO {

    private String mngNum;  // 관리번호
    private String teamNm;  // 사용부서
    private String location;   // 위치
    private String modelNm;  // 모델명
    private List<TonerPriceDTO> tonerPriceDTOList;

    public static TonerInfo2ResponseDTO of(TonerInfo tonerInfo, List<TonerPriceDTO> tonerPriceDTOs) {

        return TonerInfo2ResponseDTO.builder()
                .mngNum(tonerInfo.getMngNum())
                .teamNm(tonerInfo.getTeamNm())
                .location(tonerInfo.getLocation())
                .modelNm(tonerInfo.getModelNm())
                .tonerPriceDTOList(tonerPriceDTOs)
                .build();
    }
}
