package kr.or.kmi.mis.api.seal.model.entity;

import jakarta.persistence.*;
import kr.or.kmi.mis.api.bcd.model.entity.DraftSeqPK;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.security.Timestamp;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@ToString
@Entity
@Table(name = "hrmtexpd_hist")
@IdClass(DraftSeqPK.class)
public class SealExportHistory {

    @Id
    @Column(name = "draft_id")
    private Long draftId;

    @Id
    @Column(name = "seq_id")
    private Long seqId;

    @Column(length = 50)
    private String submission;

    @Column(length = 50)
    private String useDept;

    @Column(length = 20)
    private String expNm;

    private Timestamp expDate;

    private Timestamp returnDate;

    @Column(length = 20)
    private String corporateSeal;

    @Column(length = 20)
    private String facsimileSeal;

    @Column(length = 20)
    private String companySeal;

    @Column(length = 1000)
    private String purpose;

}
