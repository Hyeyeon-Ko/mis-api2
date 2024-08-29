package kr.or.kmi.mis.api.seal.model.request;

import kr.or.kmi.mis.api.seal.model.entity.SealRegisterDetail;
import lombok.Getter;

@Getter
public class SealRegisterRequestDTO {

    private String drafterId;
    private String sealNm;
    private String useDept;
    private String purpose;
    private String manager;
    private String subManager;
    private String draftDate;
    private String instCd;

    public SealRegisterDetail toDetailEntity(String sealImageName, String sealImagePath) {
        return SealRegisterDetail.builder()
                .sealNm(sealNm)
                .sealImage(sealImageName)
                .sealImagePath(sealImagePath)
                .useDept(useDept)
                .purpose(purpose)
                .manager(manager)
                .subManager(subManager)
                .draftDate(draftDate)
                .instCd(instCd)
                .build();
    }
}
