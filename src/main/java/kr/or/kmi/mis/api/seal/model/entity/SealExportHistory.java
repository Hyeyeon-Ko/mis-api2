package kr.or.kmi.mis.api.seal.model.entity;

import jakarta.persistence.*;
import kr.or.kmi.mis.api.bcd.model.entity.DraftSeqPK;
import lombok.*;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@ToString
@Entity
@Table(name = "hrmtexpd_hist")
@IdClass(DraftSeqPK.class)
public class SealExportHistory {

    @Id
    @Column(name = "draft_id")
    private String draftId;

    @Id
    @Column(name = "seq_id")
    private Long seqId;

    @Column(length = 50)
    private String submission;

    @Column(length = 20)
    private String expNm;

    @Column(length = 20)
    private String expDate;

    @Column(length = 20)
    private String returnDate;

    @Column(length = 20)
    private String corporateSeal;

    @Column(length = 20)
    private String facsimileSeal;

    @Column(length = 20)
    private String companySeal;

    @Column(length = 1000)
    private String purpose;

    @Column(length = 1000)
    private String notes;

    @Builder
    public SealExportHistory(SealExportDetail sealExportDetail, Long seqId) {
        this.draftId = sealExportDetail.getDraftId();
        this.seqId = seqId;
        this.submission = sealExportDetail.getSubmission();
        this.expNm = sealExportDetail.getExpNm();
        this.expDate = sealExportDetail.getExpDate();
        this.returnDate = sealExportDetail.getReturnDate();
        this.corporateSeal = sealExportDetail.getCorporateSeal();
        this.facsimileSeal = sealExportDetail.getFacsimileSeal();
        this.companySeal = sealExportDetail.getCompanySeal();
        this.purpose = sealExportDetail.getPurpose();
        this.notes = sealExportDetail.getNotes();
    }

}
