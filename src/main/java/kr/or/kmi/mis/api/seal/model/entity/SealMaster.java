package kr.or.kmi.mis.api.seal.model.entity;

import jakarta.persistence.*;
import kr.or.kmi.mis.cmm.model.entity.BaseSystemFieldEntity;
import lombok.*;

import java.sql.Timestamp;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@ToString
@Entity
@Table(name = "hrmtsealm")
public class SealMaster extends BaseSystemFieldEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long draftId;

    @Column(nullable = false)
    private Timestamp draftDate;

    private Timestamp respondDate;

    @Column(nullable = false, length = 20)
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

    private String status;

    private String division;  // A: 날인, B: 반출

    private String instCd;

    @Builder
    public SealMaster(String drafter, String drafterId, Timestamp draftDate, String status, String division, String instCd) {

        String divisionType = "A".equals(division) ? "날인" : "반출";

        this.drafter = drafter;
        this.drafterId = drafterId;
        this.draftDate = draftDate;
        this.status = status;
        this.title = String.format("인장%s 신청서 (%s)", divisionType, drafter);
        this.division = division;
        this.instCd = instCd;
    }

    public void updateStatus(String status) {
        this.status = status;
    }

    public void confirm(String status, String approver, String approverId) {
        this.status = status;
        this.respondDate = new Timestamp(System.currentTimeMillis());
        this.approver = approver;
        this.approverId = approverId;
    }

    public void reject(String status, String disapprover, String disapproverId, String rejectReason) {
        this.status = status;
        this.respondDate = new Timestamp(System.currentTimeMillis());
        this.disapprover = disapprover;
        this.disapproverId = disapproverId;
        this.rejectReason = rejectReason;
    }
}
