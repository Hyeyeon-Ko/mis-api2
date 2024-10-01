package kr.or.kmi.mis.api.seal.model.entity;

import jakarta.persistence.*;
import kr.or.kmi.mis.api.bcd.model.entity.DraftSeqPK;
import kr.or.kmi.mis.cmm.model.entity.BaseSystemFieldEntity;
import lombok.*;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@ToString
@Entity
@Table(name = "hrmtimpd_hist")
@IdClass(DraftSeqPK.class)
public class SealImprintHistory extends BaseSystemFieldEntity {

    @Id
    @Column(name = "draft_id")
    private String draftId;

    @Id
    @Column(name = "seq_id")
    private Long seqId;

    @Column(length = 50)
    private String submission;

    @Column(length = 20)
    private String useDate;

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
    public SealImprintHistory(SealImprintDetail sealImprintDetail, Long seqId) {
        this.draftId = sealImprintDetail.getDraftId();
        this.seqId = seqId;
        this.submission = sealImprintDetail.getSubmission();
        this.useDate = sealImprintDetail.getUseDate();
        this.corporateSeal = sealImprintDetail.getCorporateSeal();
        this.facsimileSeal = sealImprintDetail.getFacsimileSeal();
        this.companySeal = sealImprintDetail.getCompanySeal();
        this.purpose = sealImprintDetail.getPurpose();
        this.notes = sealImprintDetail.getNotes();
    }

}
