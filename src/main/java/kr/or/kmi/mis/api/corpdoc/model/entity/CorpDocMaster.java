package kr.or.kmi.mis.api.corpdoc.model.entity;

import jakarta.persistence.*;
import kr.or.kmi.mis.cmm.model.entity.BaseSystemFieldEntity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
@Table(name = "hrmtcorpm")
public class CorpDocMaster extends BaseSystemFieldEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long draftId;

    @Column(nullable = false)
    private Timestamp draftDate;

    @Column
    private Timestamp respondDate;

    @Column
    private Timestamp endDate;

    @Column(nullable = false, length = 20)
    private String drafter;

    @Column(nullable = false, length = 20)
    private String drafterId;

    @Column(length = 20)
    private String approver;

    @Column(length = 20)
    private String approverId;

    @Column(length = 20)
    private String disapprover;

    @Column(length = 20)
    private String disapproverId;

    @Column(length = 1000)
    private String rejectReason;

    @Column(length = 500)
    private String title;

    @Column(length = 1)
    private String status;

    @Column(length = 20)
    private String instCd;

    @Builder
    public CorpDocMaster(Timestamp draftDate, String drafter, String drafterId, String status, String instCd) {
        this.draftDate = draftDate;
        this.drafter = drafter;
        this.drafterId = drafterId;
        this.title = String.format("법인서류 신청서 (%s)", drafter);
        this.status = status;
        this.instCd = instCd;
    }

    public void updateStatus(String newStatus) {
        this.status = newStatus;
    }
}
