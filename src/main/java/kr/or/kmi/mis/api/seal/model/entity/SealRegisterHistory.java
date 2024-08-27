package kr.or.kmi.mis.api.seal.model.entity;

import jakarta.persistence.*;
import kr.or.kmi.mis.api.bcd.model.entity.DraftSeqPK;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.sql.Timestamp;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@ToString
@Entity
@Table(name = "hrmtregd_hist")
@IdClass(DraftSeqPK.class)
public class SealRegisterHistory {

    @Id
    @Column(name = "draft_id")
    private Long draftId;

    @Id
    @Column(name = "seq_id")
    private Long seqId;

    @Column(length = 20)
    private String sealNm;

    @Column(length = 100)
    private String sealImage;

    @Column(length = 50)
    private String useDept;

    @Column(length = 1000)
    private int purpose;

    @Column(length = 20)
    private String manager;

    @Column(length = 20)
    private String subManager;

    @Column(length = 20)
    private String instCd;

}
