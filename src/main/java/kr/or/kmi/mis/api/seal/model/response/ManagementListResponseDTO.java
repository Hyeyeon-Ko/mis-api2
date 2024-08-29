package kr.or.kmi.mis.api.seal.model.response;

import kr.or.kmi.mis.api.seal.model.entity.SealImprintDetail;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ManagementListResponseDTO {

    // TODO: 인장관리대장에 불러와야할 데이터 다시 확인!!
    private Long draftId;
    private String submission;
    private String useDate;
    private String corporateSeal;
    private String facsimileSeal;
    private String companySeal;
    private String purpose;
    private String notes;

    public static ManagementListResponseDTO of(SealImprintDetail sealImprintDetail) {
        return ManagementListResponseDTO.builder()
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
