package kr.or.kmi.mis.api.seal.model.response;

import kr.or.kmi.mis.api.seal.model.entity.SealImprintDetail;
import kr.or.kmi.mis.api.seal.model.entity.SealMaster;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ImprintDetailResponseDTO {

    private Long draftId;
    private String division;
    private String submission;
    private String useDate;
    private String corporateSeal;
    private String facsimileSeal;
    private String companySeal;
    private String purpose;
    private String notes;

    public static ImprintDetailResponseDTO of(SealMaster sealMaster, SealImprintDetail sealImprintDetail) {
        return ImprintDetailResponseDTO.builder()
                .draftId(sealMaster.getDraftId())
                .division(sealMaster.getDivision())
                .submission(sealImprintDetail.getSubmission())
                .useDate(sealImprintDetail.getUseDate())
                .corporateSeal(sealImprintDetail.getCorporateSeal())
                .facsimileSeal(sealImprintDetail.getFacsimileSeal())
                .companySeal(sealImprintDetail.getCompanySeal())
                .purpose(sealImprintDetail.getPurpose())
                .notes(sealImprintDetail.getNotes())
                .build();
    }
}
