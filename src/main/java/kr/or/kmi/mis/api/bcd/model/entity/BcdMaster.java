package kr.or.kmi.mis.api.bcd.model.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.sql.Timestamp;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@ToString
@Entity
@Table(name = "hrmtbcdm")
public class BcdMaster {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long draftId;

    @Column(nullable = false, length = 500)
    private String title;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private Timestamp draftDate;

    @Column(length = 20)
    private String drafterId;

    @Column(nullable = false, length = 20)
    private String drafter;

    @Column(length = 20)
    private String approverId;

    @Column(length = 20)
    private String approver;

    @Column(length = 20)
    private String disapproverId;

    @Column(length = 20)
    private String disapprover;

    @Column(length = 1000)
    private String rejectReason;

    private Timestamp respondDate;

    private Timestamp orderDate;

    private Timestamp endDate;

    private String status;

    @Builder
    public BcdMaster(String drafterId, String drafter, String teamNm, String korNm) {
        this.title = String.format("[%s] 명함신청서 (%s)", teamNm, korNm);
        this.drafterId = drafterId;
        this.drafter = drafter;
        this.status = "A";        // 명함 생성 시, A(승인대기)를 default 값으로 설정
    }

    public void updateStatus(String applyStatus) {
        this.status = applyStatus;
    }

}