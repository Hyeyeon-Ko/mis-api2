package kr.or.kmi.mis.api.corpdoc.model.request;

import kr.or.kmi.mis.api.corpdoc.model.entity.CorpDocDetail;
import kr.or.kmi.mis.api.corpdoc.model.entity.CorpDocMaster;
import lombok.Getter;

import java.time.LocalDateTime;


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

    public CorpDocMaster toMasterEntity(String draftId) {
        return CorpDocMaster.builder()
                .draftId(draftId)
                .draftDate(LocalDateTime.now())
                .drafter(drafter)
                .drafterId(drafterId)
                .status("A")
                .instCd(instCd)
                .build();
    }

    public CorpDocDetail toDetailEntity(String draftId) {
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
                .build();
    }
}
