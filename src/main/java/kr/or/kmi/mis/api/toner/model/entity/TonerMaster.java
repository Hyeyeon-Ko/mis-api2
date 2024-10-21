package kr.or.kmi.mis.api.toner.model.entity;

import jakarta.persistence.*;
import kr.or.kmi.mis.cmm.model.entity.BaseSystemFieldEntity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
@Table(name = "hrmtptnm")
public class TonerMaster extends BaseSystemFieldEntity {

    @Id
    @Column(nullable = false, length = 12)
    private String draftId;

    private LocalDateTime draftDate;

    private LocalDateTime respondDate;

    private LocalDateTime endDate;

    @Column(length = 20)
    private String drafter;

    @Column(length = 20)
    private String drafterId;

    @Column(length = 20)
    private String approver;

    @Column(length = 20)
    private String approverId;

    @Column(length = 20)
    private String disapprover;

    @Column(length = 20)
    private String disapproverId;

    @Column(length = 500)
    private String title;

    @Column(length = 1000)
    private String rejectReason;

    @Column(length = 1)
    private String status;

    @Column(length = 20)
    private String instCd;

    @Builder
    public TonerMaster(String draftId, LocalDateTime draftDate, String drafter, String drafterId, String status, String instCd) {
        this.draftId = draftId;
        this.draftDate = draftDate;
        this.drafter = drafter;
        this.drafterId = drafterId;
        this.title = String.format("토너 신청서 (%s)", drafter);
        this.status = status;
        this.instCd = instCd;
    }

    public void updateStatus(String applyStatus) {
        this.status = applyStatus;
    }
}
