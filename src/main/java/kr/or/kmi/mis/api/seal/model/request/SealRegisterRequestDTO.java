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
    private String sealImageNm;

    public SealRegisterDetail toDetailEntity(String draftId) {
        return SealRegisterDetail.builder()
                .draftId(draftId)
                .sealNm(sealNm)
                .sealImage(sealImageBase64)
                .sealImageNm(sealImageNm)
                .useDept(useDept)
                .purpose(purpose)
                .manager(manager)
                .subManager(subManager)
                .draftDate(draftDate)
                .instCd(instCd)
                .build();
    }
}
