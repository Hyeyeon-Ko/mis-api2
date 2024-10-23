package kr.or.kmi.mis.api.toner.model.response;

import kr.or.kmi.mis.api.seal.model.entity.SealMaster;
import kr.or.kmi.mis.api.seal.model.response.SealMyResponseDTO;
import kr.or.kmi.mis.api.toner.model.entity.TonerMaster;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class TonerMyResponseDTO {

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

    public TonerMyResponseDTO(String draftId, String title, LocalDateTime draftDate, LocalDateTime respondDate, String drafter, String approver, String disapprover, String applyStatus, String rejectReason, String docType) {
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

    public static TonerMyResponseDTO of(TonerMaster tonerMaster) {

        return TonerMyResponseDTO.builder()
                .draftId(tonerMaster.getDraftId())
                .title(tonerMaster.getTitle())
                .draftDate(tonerMaster.getDraftDate())
                .respondDate(tonerMaster.getRespondDate())
                .drafter(tonerMaster.getDrafter())
                .approver(tonerMaster.getApprover())
                .disapprover(tonerMaster.getDisapprover())
                .applyStatus(tonerMaster.getStatus())
                .rejectReason(tonerMaster.getRejectReason())
                .docType("토너신청")
                .build();

    }
}
