package kr.or.kmi.mis.api.corpdoc.model.response;

import kr.or.kmi.mis.api.corpdoc.model.entity.CorpDocDetail;
import kr.or.kmi.mis.api.corpdoc.model.entity.CorpDocMaster;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.text.SimpleDateFormat;

@Builder
@Data
@AllArgsConstructor
public class CorpDocIssueResponseDTO {

    private Long draftId;
    private String draftDate;
    private String useDate;
    private String issueDate;
    private String drafter;
    private String instNm;
    private String status;
    private String submission;
    private String purpose;
    private int certCorpseal;
    private int totalCorpseal;
    private int certCoregister;
    private int totalCoregister;

    public static CorpDocIssueResponseDTO of(CorpDocMaster corpDocMaster, CorpDocDetail corpDocDetail) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        return CorpDocIssueResponseDTO.builder()
                .draftId(corpDocMaster.getDraftId())
                .draftDate(sdf.format(corpDocMaster.getDraftDate()))
                .useDate(corpDocDetail.getUseDate())
                .issueDate(corpDocDetail.getIssueDate())
                .drafter(corpDocMaster.getDrafter())
                .status(corpDocMaster.getStatus())
                .submission(corpDocDetail.getSubmission())
                .purpose(corpDocDetail.getPurpose())
                .certCorpseal(corpDocDetail.getCertCorpseal())
                .totalCorpseal(corpDocDetail.getTotalCorpseal())
                .certCoregister(corpDocDetail.getCertCoregister())
                .totalCoregister(corpDocDetail.getTotalCoregister())
                .build();

    }

}
