package kr.or.kmi.mis.api.corpdoc.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import kr.or.kmi.mis.cmm.model.entity.BaseSystemFieldEntity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
@Table(name = "hrmtcorpm")
public class CorpDocMaster extends BaseSystemFieldEntity {

    @Id
    @Column(nullable = false, length = 12)
    private String draftId;

    @Column(nullable = false)
    private LocalDateTime draftDate;

    @Column
    private LocalDateTime respondDate;

    @Column
    private LocalDateTime endDate;

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
    public CorpDocMaster(String draftId, LocalDateTime draftDate, String drafter, String drafterId, String status, String instCd) {
        this.draftId = draftId;
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

    public void approve(String approver, String approverId) {
        this.status = "B";
        this.approver = approver;
        this.approverId = approverId;
        this.respondDate = LocalDateTime.now();
    }

    public void disapprove(String disapprover, String disapproverId, String rejectReason) {
        this.status = "C";
        this.disapprover = disapprover;
        this.disapproverId = disapproverId;
        this.rejectReason = rejectReason;
        this.respondDate = LocalDateTime.now();
    }

    public void end(String draftId) {
        this.status = "E";
        this.endDate = LocalDateTime.now();
    }
}
