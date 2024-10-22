package kr.or.kmi.mis.api.toner.model.request;

import kr.or.kmi.mis.api.toner.model.entity.TonerInfo;
import lombok.Getter;

@Getter
public class TonerInfoRequestDTO {

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

    public TonerInfo toEntity(String instCd) {
        return TonerInfo.builder()
                .mngNum(mngNum)
                .floor(floor)
                .teamNm(teamNm)
                .manager(manager)
                .subManager(subManager)
                .location(location)
                .productNm(productNm)
                .modelNm(modelNm)
                .sn(sn)
                .company(company)
                .manuDate(manuDate)
                .tonerNm(tonerNm)
                .instCd(instCd)
                .build();
    }


}
