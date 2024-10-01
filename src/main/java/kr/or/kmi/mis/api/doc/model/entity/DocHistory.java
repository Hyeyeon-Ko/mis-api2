package kr.or.kmi.mis.api.doc.model.entity;

import jakarta.persistence.*;
import kr.or.kmi.mis.api.bcd.model.entity.DraftSeqPK;
import kr.or.kmi.mis.cmm.model.entity.BaseSystemFieldEntity;
import lombok.*;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@ToString
@Entity
@Table(name = "hrmtdocd_hist")
@IdClass(DraftSeqPK.class)
public class DocHistory extends BaseSystemFieldEntity {

    @Id
    @Column(name = "draft_id")
    private String draftId;

    @Id
    @Column(name = "seq_id")
    private Long seqId;

    @Column(length = 1)
    private String division;

    @Column(length = 20)
    private String receiver;

    @Column(length = 20)
    private String sender;

    @Column(length = 500)
    private String docTitle;

    @Column(length = 1000)
    private String purpose;

    @Builder
    public DocHistory(DocDetail docDetail, Long seqId) {
        this.draftId = docDetail.getDraftId();
        this.seqId = seqId;
        this.division = docDetail.getDivision();
        this.receiver = docDetail.getReceiver();
        this.sender = docDetail.getSender();
        this.docTitle = docDetail.getDocTitle();
        this.purpose = docDetail.getPurpose();
    }
}
