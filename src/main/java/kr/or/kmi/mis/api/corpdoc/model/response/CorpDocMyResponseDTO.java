package kr.or.kmi.mis.api.corpdoc.model.response;

import kr.or.kmi.mis.api.corpdoc.model.entity.CorpDocMaster;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@Builder
@Data
@AllArgsConstructor
public class CorpDocMyResponseDTO {

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

    public static CorpDocMyResponseDTO of(CorpDocMaster corpDocMaster) {

        return CorpDocMyResponseDTO.builder()
                .draftId(corpDocMaster.getDraftId())
                .title(corpDocMaster.getTitle())
                .draftDate(corpDocMaster.getDraftDate())
                .respondDate(corpDocMaster.getRespondDate())
                .drafter(corpDocMaster.getDrafter())
                .approver(corpDocMaster.getApprover())
                .disapprover(corpDocMaster.getDisapprover())
                .applyStatus(corpDocMaster.getStatus())
                .rejectReason(corpDocMaster.getRejectReason())
                .docType("법인서류")
                .build();
    }
}
