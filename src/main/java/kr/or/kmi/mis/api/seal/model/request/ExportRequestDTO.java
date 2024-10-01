package kr.or.kmi.mis.api.seal.model.request;

import kr.or.kmi.mis.api.seal.model.entity.SealExportDetail;
import kr.or.kmi.mis.api.seal.model.entity.SealMaster;
import lombok.Getter;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@Getter
public class ExportRequestDTO {

    private String drafter;
    private String drafterId;
    private String submission;
    private String expNm;
    private String expDate;
    private String returnDate;
    private String corporateSeal;
    private String facsimileSeal;
    private String companySeal;
    private String purpose;
    private String notes;
    private String instCd;

    public SealMaster toMasterEntity(String draftId) {

        return SealMaster.builder()
                .drafter(drafter)
                .drafter(drafter)
                .drafterId(drafterId)
                .draftDate(LocalDateTime.now())
                .status("A")
                .division("B")
                .instCd(instCd)
                .build();
    }

    public SealExportDetail toDetailEntity(String draftId) {
        return SealExportDetail.builder()
                .draftId(draftId)
                .submission(submission)
                .expNm(expNm)
                .expDate(expDate)
                .returnDate(returnDate)
                .corporateSeal(corporateSeal)
                .facsimileSeal(facsimileSeal)
                .companySeal(companySeal)
                .purpose(purpose)
                .notes(notes)
                .build();
    }
}
