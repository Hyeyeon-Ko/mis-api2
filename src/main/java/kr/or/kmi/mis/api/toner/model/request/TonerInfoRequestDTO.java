package kr.or.kmi.mis.api.toner.model.request;

import kr.or.kmi.mis.api.toner.model.entity.TonerInfo;
import lombok.Getter;

@Getter
public class TonerInfoRequestDTO {

    private final String mngNum;
    private final String floor;
    private final String teamNm;
    private final String manager;
    private final String subManager;
    private final String location;
    private final String productNm;
    private final String modelNm;
    private final String sn;
    private final String company;
    private final String manuDate;
    private final String tonerNm;

    public TonerInfoRequestDTO(String mngNum, String floor, String teamNm, String manager,
                               String subManager, String location, String productNm, String modelNm,
                               String sn, String company, String manuDate, String tonerNm) {
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
    }

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
