package kr.or.kmi.mis.api.seal.model.request;

import kr.or.kmi.mis.api.seal.model.entity.SealExportDetail;
import kr.or.kmi.mis.api.seal.model.entity.SealMaster;
import lombok.Getter;

import java.sql.Timestamp;

@Getter
public class ExportRequestDTO {

    private String drafter;
    private String drafterId;
    private String submission;
    private String useDept;
    private String expNm;
    private String expDate;
    private String returnDate;
    private String corporateSeal;
    private String facsimileSeal;
    private String companySeal;
    private String purpose;
    private String instCd;

    public SealMaster toMasterEntity() {

        return SealMaster.builder()
                .drafter(drafter)
                .drafterId(drafterId)
                .draftDate(new Timestamp(System.currentTimeMillis()))
                .status("A")
                .division("B")
                .instCd(instCd)
                .build();
    }

    public SealExportDetail toDetailEntity(Long draftId) {
        return SealExportDetail.builder()
                .draftId(draftId)
                .submission(submission)
                .useDept(useDept)
                .expNm(expNm)
                .expDate(expDate)
                .returnDate(returnDate)
                .corporateSeal(corporateSeal)
                .facsimileSeal(facsimileSeal)
                .companySeal(companySeal)
                .purpose(purpose)
                .build();
    }
}
