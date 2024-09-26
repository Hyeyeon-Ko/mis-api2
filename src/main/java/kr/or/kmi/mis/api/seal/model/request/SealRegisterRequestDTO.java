package kr.or.kmi.mis.api.seal.model.request;

import kr.or.kmi.mis.api.seal.model.entity.SealRegisterDetail;
import lombok.Data;

@Data
public class SealRegisterRequestDTO {

    private String drafterId;
    private String sealNm;
    private String useDept;
    private String purpose;
    private String manager;
    private String subManager;
    private String draftDate;
    private String instCd;

    private String sealImageBase64;

    public SealRegisterDetail toDetailEntity() {
        return SealRegisterDetail.builder()
                .sealNm(sealNm)
                .sealImage(sealImageBase64)
                .useDept(useDept)
                .purpose(purpose)
                .manager(manager)
                .subManager(subManager)
                .draftDate(draftDate)
                .instCd(instCd)
                .build();
    }
}
