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
public class CorpDocRnpResponseDTO {

    private Long draftId;
    private String drafter;
    private String draftDate;
    private String endDate;
    private String submission;
    private String purpose;
    private int certCorpseal;
    private int certCoregister;
    private int certUsesignet;
    private int warrant;

    public static CorpDocRnpResponseDTO of(CorpDocMaster corpDocMaster, CorpDocDetail corpDocDetail) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        return CorpDocRnpResponseDTO.builder()
                .draftId(corpDocMaster.getDraftId())
                .drafter(corpDocMaster.getDrafter())
                .draftDate(sdf.format(corpDocMaster.getDraftDate()))
                .endDate(sdf.format(corpDocMaster.getEndDate()))
                .submission(corpDocDetail.getSubmission())
                .purpose(corpDocDetail.getPurpose())
                .certCorpseal(corpDocDetail.getCertCorpseal())
                .certCoregister(corpDocDetail.getCertCoregister())
                .certUsesignet(corpDocDetail.getCertUsesignet())
                .warrant(corpDocDetail.getWarrant())
                .build();

    }
}
