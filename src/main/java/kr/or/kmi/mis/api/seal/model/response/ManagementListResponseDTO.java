package kr.or.kmi.mis.api.seal.model.response;

import kr.or.kmi.mis.api.seal.model.entity.SealImprintDetail;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ManagementListResponseDTO {

    private String draftId;
    private String drafter;
    private String submission;
    private String useDate;
    private String corporateSeal;
    private String facsimileSeal;
    private String companySeal;
    private String purpose;

    public ManagementListResponseDTO(String draftId, String drafter, String submission, String useDate, String corporateSeal, String facsimileSeal, String companySeal, String purpose, String notes) {
        this.draftId = draftId;
        this.drafter = drafter;
        this.submission = submission;
        this.useDate = useDate;
        this.corporateSeal = corporateSeal;
        this.facsimileSeal = facsimileSeal;
        this.companySeal = companySeal;
        this.purpose = purpose;
        this.notes = notes;
    }

    private String notes;

    public static ManagementListResponseDTO of(SealImprintDetail sealImprintDetail, String drafter) {
        return ManagementListResponseDTO.builder()
                .draftId(sealImprintDetail.getDraftId())
                .drafter(drafter)
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
