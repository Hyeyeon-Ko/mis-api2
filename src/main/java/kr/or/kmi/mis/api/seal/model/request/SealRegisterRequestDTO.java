package kr.or.kmi.mis.api.seal.model.request;

import kr.or.kmi.mis.api.seal.model.entity.SealRegisterDetail;
import lombok.Getter;

@Getter
public class SealRegisterRequestDTO {

    private String drafterId;
    private String sealNm;
    private String sealImage;
    private String useDept;
    private String purpose;
    private String manager;
    private String subManager;
    private String instCd;

    public SealRegisterDetail toDetailEntity() {
        return SealRegisterDetail.builder()
                .sealNm(sealNm)
                .sealImage(sealImage)
                .useDept(useDept)
                .purpose(purpose)
                .manager(manager)
                .subManager(subManager)
                .instCd(instCd)
                .build();
    }
}
