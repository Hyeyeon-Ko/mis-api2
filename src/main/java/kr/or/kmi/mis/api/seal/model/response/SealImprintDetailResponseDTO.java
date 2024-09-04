package kr.or.kmi.mis.api.seal.model.response;

import kr.or.kmi.mis.api.seal.model.entity.SealImprintDetail;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SealImprintDetailResponseDTO {

    private Long draftId;
    private String submission;
    private String useDate;
    private String corporateSeal;
    private String facsimileSeal;
    private String companySeal;
    private String purpose;
    private String notes;

    public static SealImprintDetailResponseDTO of(SealImprintDetail sealImprintDetail) {
        return SealImprintDetailResponseDTO.builder()
                .draftId(sealImprintDetail.getDraftId())
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
