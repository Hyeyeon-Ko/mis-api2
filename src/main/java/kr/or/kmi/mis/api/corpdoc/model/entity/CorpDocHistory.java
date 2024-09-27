package kr.or.kmi.mis.api.corpdoc.model.entity;

import jakarta.persistence.*;
import kr.or.kmi.mis.cmm.model.entity.BaseSystemFieldEntity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
@Table(name = "hrmtcorpd_hist")
@IdClass(DraftSeqPK.class)
public class CorpDocHistory extends BaseSystemFieldEntity {

    @Id
    @Column(name = "draft_id")
    private Long draftId;

    @Id
    @Column(name = "seq_id")
    private Long seqId;

    @Column(length = 50)
    private String submission;

    @Column(length = 1000)
    private String purpose;

    @Column(length = 20)
    private String useDate;

    @Column
    private int certCorpseal;

    @Column
    private int certCoregister;

    @Column
    private int certUsesignet;

    @Column
    private int warrant;

    @Column(length = 1)
    private String type;

    @Column(length = 1000)
    private String notes;

    @Builder
    public CorpDocHistory(CorpDocDetail corpDocDetail, Long seqId) {
        this.draftId = corpDocDetail.getDraftId();
        this.seqId = seqId;
        this.submission = corpDocDetail.getSubmission();
        this.purpose = corpDocDetail.getPurpose();
        this.useDate = corpDocDetail.getUseDate();
        this.certCorpseal = corpDocDetail.getCertCorpseal();
        this.certCoregister = corpDocDetail.getCertCoregister();
        this.certUsesignet = corpDocDetail.getCertUsesignet();
        this.warrant = corpDocDetail.getWarrant();
        this.type = corpDocDetail.getType();
        this.notes = corpDocDetail.getNotes();
    }
}
