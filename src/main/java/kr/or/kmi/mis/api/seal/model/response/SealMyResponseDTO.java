package kr.or.kmi.mis.api.seal.model.response;

import kr.or.kmi.mis.api.seal.model.entity.SealMaster;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class SealMyResponseDTO {

    private String draftId;
    private String title;
    private LocalDateTime draftDate;
    private LocalDateTime respondDate;
    private String drafter;
    private String approver;
    private String disapprover;
    private String applyStatus;
    private String rejectReason;
    private String docType;

    public SealMyResponseDTO(String draftId, String title, LocalDateTime draftDate, LocalDateTime respondDate, String drafter, String approver, String disapprover, String applyStatus, String rejectReason, String docType) {
        this.draftId = draftId;
        this.title = title;
        this.draftDate = draftDate;
        this.respondDate = respondDate;
        this.drafter = drafter;
        this.approver = approver;
        this.disapprover = disapprover;
        this.applyStatus = applyStatus;
        this.rejectReason = rejectReason;
        this.docType = docType;
    }

    public static SealMyResponseDTO of(SealMaster sealMaster) {

        String docType = "A".equals(sealMaster.getDivision()) ? "인장신청(날인)" : "인장신청(반출)";

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
