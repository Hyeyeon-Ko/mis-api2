package kr.or.kmi.mis.api.seal.model.entity;

import jakarta.persistence.*;
import kr.or.kmi.mis.api.bcd.model.entity.DraftSeqPK;
import kr.or.kmi.mis.cmm.model.entity.BaseSystemFieldEntity;
import lombok.*;

import java.time.LocalDateTime;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@ToString
@Entity
@Table(name = "hrmtregd_hist")
@IdClass(DraftSeqPK.class)
public class SealRegisterHistory extends BaseSystemFieldEntity {

    @Id
    @Column(name = "draft_id")
    private String draftId;

    @Id
    @Column(name = "seq_id")
    private Long seqId;

    @Column(length = 20)
    private String sealNm;

    @Column(name = "seal_image", columnDefinition = "longtext")
    private String sealImage;

    @Column(length = 50)
    private String useDept;

    @Column(length = 1000)
    private String purpose;

    @Column(length = 20)
    private String manager;

    @Column(length = 20)
    private String subManager;

    @Column(length = 20)
    private String draftDate;

    @Column
    private LocalDateTime changedDate;

    @Column(length = 20)
    private String instCd;

    @Builder
    public SealRegisterHistory(SealRegisterDetail sealRegisterDetail, Long seqId) {
        this.draftId = sealRegisterDetail.getDraftId();
        this.seqId = seqId;
        this.sealNm = sealRegisterDetail.getSealNm();
        this.sealImage = sealRegisterDetail.getSealImage();
        this.useDept = sealRegisterDetail.getUseDept();
        this.purpose = sealRegisterDetail.getPurpose();
        this.manager = sealRegisterDetail.getManager();
        this.subManager = sealRegisterDetail.getSubManager();
        this.draftDate = sealRegisterDetail.getDraftDate();
        this.instCd = sealRegisterDetail.getInstCd();
    }

    public void update(LocalDateTime changedDate) {
        this.changedDate = changedDate;
    }
}
