package kr.or.kmi.mis.api.seal.model.request;

import kr.or.kmi.mis.api.seal.model.entity.SealImprintDetail;
import kr.or.kmi.mis.api.seal.model.entity.SealMaster;
import lombok.Getter;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@Getter
public class ImprintRequestDTO {

    private String drafter;
    private String drafterId;
    private String submission;
    private String useDate;
    private String corporateSeal;
    private String facsimileSeal;
    private String companySeal;
    private String purpose;
    private String notes;
    private String instCd;

    public SealMaster toMasterEntity(String draftId) {
        return SealMaster.builder()
                .draftId(draftId)
                .drafter(drafter)
                .drafterId(drafterId)
                .draftDate(LocalDateTime.now())
                .status("A")
                .division("A")
                .instCd(instCd)
                .build();
    }

    public SealImprintDetail toDetailEntity(String draftId) {
        return SealImprintDetail.builder()
                .draftId(draftId)
                .submission(submission)
                .useDate(useDate)
                .corporateSeal(corporateSeal)
                .facsimileSeal(facsimileSeal)
                .companySeal(companySeal)
                .purpose(purpose)
                .notes(notes)
                .build();
    }
}
