package kr.or.kmi.mis.api.toner.model.response;

import kr.or.kmi.mis.api.toner.model.entity.TonerInfo;
import kr.or.kmi.mis.api.toner.model.entity.TonerPrice;
import lombok.Builder;
import lombok.Data;

@Data
public class TonerExcelResponseDTO {

    private String mngNum;
    private String floor;
    private String teamNm;
    private String manager;
    private String subManager;
    private String location;
    private String productNm;
    private String modelNm;
    private String sn;
    private String company;
    private String manuDate;
    private String tonerNm;
    private String price;
    private String color;

    @Builder
    public TonerExcelResponseDTO(String mngNum, String floor, String teamNm, String manager, String subManager,
                                 String location, String productNm, String modelNm, String sn, String company,
                                 String manuDate, String tonerNm, String price, String color) {
        this.mngNum = mngNum;
        this.floor = floor;
        this.teamNm = teamNm;
        this.manager = manager;
        this.subManager = subManager;
        this.location = location;
        this.productNm = productNm;
        this.modelNm = modelNm;
        this.sn = sn;
        this.company = company;
        this.manuDate = manuDate;
        this.tonerNm = tonerNm;
        this.price = price;
        this.color = color;
    }

    public static TonerExcelResponseDTO of(TonerInfo tonerInfo, TonerPrice tonerPrice) {
        return TonerExcelResponseDTO.builder()
                .mngNum(tonerInfo.getMngNum())
                .floor(tonerInfo.getFloor())
                .teamNm(tonerInfo.getTeamNm())
                .manager(tonerInfo.getManager())
                .subManager(tonerInfo.getSubManager())
                .location(tonerInfo.getLocation())
                .productNm(tonerInfo.getProductNm())
                .modelNm(tonerInfo.getModelNm())
                .sn(tonerInfo.getSn())
                .company(tonerInfo.getCompany())
                .manuDate(tonerInfo.getManuDate())
                .tonerNm(tonerInfo.getTonerNm())
                .price(tonerPrice != null ? tonerPrice.getPrice() : null)
                .color(tonerInfo.getColor())
                .build();
    }
}
