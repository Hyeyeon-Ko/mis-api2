package kr.or.kmi.mis.api.seal.model.response;

import kr.or.kmi.mis.api.seal.model.entity.SealMaster;
import lombok.Builder;
import lombok.Data;

import java.sql.Timestamp;

@Data
@Builder
public class SealMyResponseDTO {

    private Long draftId;
    private String title;
    private Timestamp draftDate;
    private Timestamp respondDate;
    private String drafter;
    private String approver;
    private String disapprover;
    private String applyStatus;
    private String rejectReason;
    private String docType;

    public static SealMyResponseDTO of(SealMaster sealMaster) {

        String docType = "A".equals(sealMaster.getDivision()) ? "날인신청" : "반출신청";

        return SealMyResponseDTO.builder()
                .draftId(sealMaster.getDraftId())
                .title(sealMaster.getTitle())
                .draftDate(sealMaster.getDraftDate())
                .respondDate(sealMaster.getRespondDate())
                .drafter(sealMaster.getDrafter())
                .approver(sealMaster.getApprover())
                .disapprover(sealMaster.getDisapprover())
                .applyStatus(sealMaster.getStatus())
                .rejectReason(sealMaster.getRejectReason())
                .docType(docType)
                .build();

    }
}
