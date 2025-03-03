package kr.or.kmi.mis.api.seal.model.response;

import kr.or.kmi.mis.api.seal.model.entity.SealRegisterDetail;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SealDetailResponseDTO {

    private String draftId;
    private String sealNm;
    private String sealImage;
    private String sealImageNm;
    private String useDept;
    private String purpose;
    private String manager;
    private String subManager;
    private String draftDate;

    public static SealDetailResponseDTO of(SealRegisterDetail sealRegisterDetail) {
        return SealDetailResponseDTO.builder()
                .draftId(sealRegisterDetail.getDraftId())
                .sealNm(sealRegisterDetail.getSealNm())
                .sealImage(sealRegisterDetail.getSealImage())
                .sealImageNm(sealRegisterDetail.getSealImageNm())
                .useDept(sealRegisterDetail.getUseDept())
                .purpose(sealRegisterDetail.getPurpose())
                .manager(sealRegisterDetail.getManager())
                .subManager(sealRegisterDetail.getSubManager())
                .draftDate(sealRegisterDetail.getDraftDate())
                .build();
    }
}
