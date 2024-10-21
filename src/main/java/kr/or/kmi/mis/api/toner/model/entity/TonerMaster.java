package kr.or.kmi.mis.api.toner.model.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
@Table(name = "hrmtptnm")
public class TonerMaster {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long draftId;

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
}
