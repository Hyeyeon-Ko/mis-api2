package kr.or.kmi.mis.api.seal.model.response;

import kr.or.kmi.mis.api.seal.model.entity.SealRegisterDetail;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TotalRegistrationListResponseDTO {

    private String draftId;
    private String sealNm;
    private String sealImage;
    private String useDept;
    private String purpose;
    private String manager;
    private String subManager;
    private String draftDate;

    public TotalRegistrationListResponseDTO(String draftId, String sealNm, String sealImage, String useDept, String purpose, String manager, String subManager, String draftDate, String instCd) {
        this.draftId = draftId;
        this.sealNm = sealNm;
        this.sealImage = sealImage;
        this.useDept = useDept;
        this.purpose = purpose;
        this.manager = manager;
        this.subManager = subManager;
        this.draftDate = draftDate;
        this.instCd = instCd;
    }

    private String instCd;

    public static TotalRegistrationListResponseDTO of(SealRegisterDetail sealRegisterDetail) {
        return TotalRegistrationListResponseDTO.builder()
                .draftId(sealRegisterDetail.getDraftId())
                .sealNm(sealRegisterDetail.getSealNm())
                .sealImage(sealRegisterDetail.getSealImage())
                .useDept(sealRegisterDetail.getUseDept())
                .purpose(sealRegisterDetail.getPurpose())
                .manager(sealRegisterDetail.getManager())
                .subManager(sealRegisterDetail.getSubManager())
                .draftDate(sealRegisterDetail.getDraftDate())
                .instCd(sealRegisterDetail.getInstCd())
                .build();
    }
}
