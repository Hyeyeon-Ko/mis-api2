package kr.or.kmi.mis.api.corpdoc.model.response;

import kr.or.kmi.mis.api.corpdoc.model.entity.CorpDocDetail;
import kr.or.kmi.mis.api.corpdoc.model.entity.CorpDocMaster;
import lombok.*;

import java.time.LocalDateTime;

@Builder
@Data
@AllArgsConstructor
public class CorpDocIssueResponseDTO {

    private String draftId;
    private LocalDateTime draftDate;
    private String useDate;
    private LocalDateTime issueDate;
    private String drafter;
    private String instCd;
    private String instNm;
    private String status;
    private String submission;
    private String purpose;
    private int certCorpseal;
    private int totalCorpseal;
    private int certCoregister;
    private int totalCoregister;
    private int certUsesignet;
    private int warrant;
    private String note;

    public static CorpDocIssueResponseDTO of(CorpDocMaster corpDocMaster, CorpDocDetail corpDocDetail) {

        return CorpDocIssueResponseDTO.builder()
                .draftId(corpDocMaster.getDraftId())
                .draftDate(corpDocMaster.getDraftDate())
                .useDate(corpDocDetail.getUseDate())
                .issueDate(corpDocDetail.getIssueDate())
                .drafter(corpDocMaster.getDrafter())
                .instCd(corpDocMaster.getInstCd())
                .status(corpDocMaster.getStatus())
                .submission(corpDocDetail.getSubmission())
                .purpose(corpDocDetail.getPurpose())
                .certCorpseal(corpDocDetail.getCertCorpseal())
                .totalCorpseal(corpDocDetail.getTotalCorpseal())
                .certCoregister(corpDocDetail.getCertCoregister())
                .totalCoregister(corpDocDetail.getTotalCoregister())
                .certUsesignet(corpDocDetail.getCertUsesignet())
                .warrant(corpDocDetail.getWarrant())
                .note("P".equals(corpDocDetail.getType()) ? "PDF" : "")
                .build();

    }

}
