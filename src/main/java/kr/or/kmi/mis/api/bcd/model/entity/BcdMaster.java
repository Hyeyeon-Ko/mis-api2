package kr.or.kmi.mis.api.bcd.model.entity;

import jakarta.persistence.*;
import kr.or.kmi.mis.api.confirm.model.request.ApproveRequestDTO;
import kr.or.kmi.mis.api.confirm.model.request.DisapproveRequestDTO;
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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long draftId;

    @CreationTimestamp
    @Column(nullable = false)
    private Timestamp draftDate;

    private Timestamp respondDate;

    private Timestamp orderDate;

    private Timestamp endDate;

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

    @Column(length = 500)
    private String title;

    @Column(length = 1000)
    private String rejectReason;

    private String status;

    @Builder
    public BcdMaster(String drafterId, String drafter, String teamNm, String korNm) {
        this.title = String.format("[%s]명함신청서(%s)", teamNm, korNm);
        this.drafterId = drafterId;
        this.drafter = drafter;
        this.status = "A";        // 명함 생성 시, A(승인대기)를 default 값으로 설정
    }

    public void updateDate(Timestamp draftDate) {
        this.draftDate = draftDate;
    }

    public void updateStatus(String applyStatus) {
        this.status = applyStatus;
    }

    // 승인 -> 승인자, 대응일시, 상태 업데이트
    public void updateApprove(ApproveRequestDTO approveRequestDTO) {
        this.approver = approveRequestDTO.getApprover();
        this.approverId = approveRequestDTO.getApproverId();
        this.respondDate = approveRequestDTO.getRespondDate();
        this.status = approveRequestDTO.getStatus();
    }

    // 반려 -> 반려자, 대응일시, 상태 업데이트
    public void updateDisapprove(DisapproveRequestDTO disapproveRequestDTO) {
        this.disapprover = disapproveRequestDTO.getDisapprover();
        this.disapproverId = disapproveRequestDTO.getDisapproverId();
        this.rejectReason = disapproveRequestDTO.getRejectReason();
        this.respondDate = disapproveRequestDTO.getRespondDate();
        this.status = disapproveRequestDTO.getStatus();
    }


    // 발주 -> 발주일시 업데이트
    public void updateOrder(Timestamp deletedt) {
        this.orderDate = deletedt;
        this.status = "D";
    }
}