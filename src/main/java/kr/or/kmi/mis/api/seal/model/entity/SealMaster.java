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

    private Timestamp endDate;

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

    @Builder
    public SealMaster(String drafter, String drafterId, Timestamp draftDate, String status, String title) {
        this.drafter = drafter;
        this.drafterId = drafterId;
        this.draftDate = draftDate;
        this.status = status;
        this.title = String.format("인장신청 (%s)", drafter);;
    }

    public void updateStatus(String status) {
        this.status = status;
    }
}
