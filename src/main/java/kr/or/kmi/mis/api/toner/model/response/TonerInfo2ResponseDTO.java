package kr.or.kmi.mis.api.toner.model.response;

import kr.or.kmi.mis.api.toner.model.entity.TonerInfo;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TonerInfo2ResponseDTO {

    private String mngNum;  // 관리번호
    private String teamNm;  // 사용부서
    private String location;   // 위치
    private String modelNm;  // 모델명
    private String tonerNm;  // 토너명
    private String color;    // 색상
    private String price;    // 가격


    public static TonerInfo2ResponseDTO of(TonerInfo tonerInfo, String price) {

        return TonerInfo2ResponseDTO.builder()
                .mngNum(tonerInfo.getMngNum())
                .teamNm(tonerInfo.getTeamNm())
                .location(tonerInfo.getLocation())
                .modelNm(tonerInfo.getModelNm())
                .tonerNm(tonerInfo.getTonerNm())
                .color(tonerInfo.getColor())
                .price(price)
                .build();
    }
}
