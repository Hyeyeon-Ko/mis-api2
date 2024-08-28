package kr.or.kmi.mis.api.corpdoc.model.request;

import kr.or.kmi.mis.api.corpdoc.model.entity.CorpDocDetail;
import kr.or.kmi.mis.api.corpdoc.model.entity.CorpDocMaster;
import lombok.Getter;

import java.sql.Timestamp;

@Getter
public class CorpDocRequestDTO {

    private String drafter;
    private String drafterId;
    private String instCd;
    private String submission;
    private String purpose;
    private String useDate;
    private int certCorpseal;
    private int certCoregister;
    private int certUsesignet;
    private int warrant;
    private String type;
    private String notes;

    public CorpDocMaster toMasterEntity() {
        return CorpDocMaster.builder()
                .draftDate(new Timestamp(System.currentTimeMillis()))
                .drafter(drafter)
                .drafterId(drafterId)
                .status("A")
                .instCd(instCd)
                .build();
    }

    public CorpDocDetail toDetailEntity(Long draftId, String fileName, String filePath) {
        return CorpDocDetail.builder()
                .draftId(draftId)
                .submission(submission)
                .purpose(purpose)
                .useDate(useDate)
                .certCorpseal(certCorpseal)
                .certCoregister(certCoregister)
                .certUsesignet(certUsesignet)
                .warrant(warrant)
                .type(type)
                .notes(notes)
                .fileName(fileName)
                .filePath(filePath)
                .build();
    }
}
