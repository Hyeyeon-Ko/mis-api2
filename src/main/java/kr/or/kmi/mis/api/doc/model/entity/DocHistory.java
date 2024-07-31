package kr.or.kmi.mis.api.doc.model.entity;

import jakarta.persistence.*;
import kr.or.kmi.mis.api.bcd.model.entity.DraftSeqPK;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@ToString
@Entity
@Table(name = "hrmtdocd_hist")
@IdClass(DraftSeqPK.class)
public class DocHistory {

    @Id
    @Column(name = "draft_id")
    private Long draftId;

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

}
