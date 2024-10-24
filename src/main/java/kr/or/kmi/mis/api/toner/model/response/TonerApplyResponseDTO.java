package kr.or.kmi.mis.api.toner.model.response;

import kr.or.kmi.mis.api.toner.model.entity.TonerDetail;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TonerApplyResponseDTO {

    private String mngNum;
    private String teamNm;
    private String location;
    private String printNm;
    private String tonerNm;
    private String price;
    private int quantity;
    private String totalPrice;

    public static TonerApplyResponseDTO of(TonerDetail tonerDetail) {
        return TonerApplyResponseDTO.builder()
                .mngNum(tonerDetail.getMngNum())
                .teamNm(tonerDetail.getTeamNm())
                .location(tonerDetail.getLocation())
                .printNm(tonerDetail.getPrintNm())
                .tonerNm(tonerDetail.getTonerNm())
                .price(tonerDetail.getPrice())
                .quantity(tonerDetail.getQuantity())
                .totalPrice(tonerDetail.getTotalPrice())
                .build();
    }
}
