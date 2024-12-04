package kr.or.kmi.mis.api.bcd.model.response;

import kr.or.kmi.mis.api.bcd.model.entity.BcdMaster;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Builder
@Data
@AllArgsConstructor
public class BcdMyResponseDTO {

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

    public static BcdMyResponseDTO of(BcdMaster bcdMaster) {
        return BcdMyResponseDTO.builder()
                .draftId(bcdMaster.getDraftId())
                .title(bcdMaster.getTitle())
                .draftDate(bcdMaster.getDraftDate())
                .respondDate(bcdMaster.getRespondDate())
                .drafter(bcdMaster.getDrafter())
                .approver(bcdMaster.getApprover())
                .disapprover(bcdMaster.getDisapprover())
                .applyStatus(bcdMaster.getStatus())
                .rejectReason(bcdMaster.getRejectReason())
                .docType("명함신청")
                .build();
    }

}
