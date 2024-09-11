package kr.or.kmi.mis.api.bcd.model.entity;

import jakarta.persistence.*;
import kr.or.kmi.mis.api.bcd.model.request.BcdUpdateRequestDTO;
import kr.or.kmi.mis.api.confirm.model.request.BcdApproveRequestDTO;
import kr.or.kmi.mis.api.confirm.model.request.BcdDisapproveRequestDTO;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.sql.Timestamp;
import java.util.List;

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

    @Column(length = 1000)
    private String approverChain;

    @Column(nullable = false)
    private Integer currentApproverIndex;

    private String status;

    @Builder
    public BcdMaster(String drafterId, String drafter, String teamNm, String korNm, List<String> approvers) {
        this.title = String.format("[%s]명함신청서(%s)", teamNm, korNm);
        this.drafterId = drafterId;
        this.drafter = drafter;
        this.approverChain = String.join(",", approvers); // 결재자 리스트를 ','로 구분하여 저장
        this.currentApproverIndex = 0; // 첫 번째 결재자부터 시작
        this.status = "A"; // 초기 상태는 승인 대기
    }

    // 현재 결재자 정보를 반환하는 메서드
    public String getCurrentApproverId() {
        return this.approverChain.split(",")[this.currentApproverIndex];
    }

    // 결재 승인 처리 메서드
    public void approveCurrentApproverId() {
        if (this.currentApproverIndex < this.approverChain.split(",").length - 1) {
            this.currentApproverIndex++; // 다음 결재자로 이동
        } else {
            this.status = "A"; // 모든 결재자가 승인되면 상태 완료로 변경
        }
    }

    public void updateEndDate(Timestamp endDate) {
        this.endDate = endDate;
    }

    public void updateStatus(String applyStatus) {
        this.status = applyStatus;
    }

    // 승인 -> 승인자, 대응일시, 상태 업데이트
    public void updateApprove(BcdApproveRequestDTO bcdApproveRequestDTO) {
        this.approver = bcdApproveRequestDTO.getApprover();
        this.approverId = bcdApproveRequestDTO.getApproverId();
        this.respondDate = bcdApproveRequestDTO.getRespondDate();
        this.status = bcdApproveRequestDTO.getStatus();
    }

    // 반려 -> 반려자, 대응일시, 상태 업데이트
    public void updateDisapprove(BcdDisapproveRequestDTO bcdDisapproveRequestDTO) {
        this.disapprover = bcdDisapproveRequestDTO.getDisapprover();
        this.disapproverId = bcdDisapproveRequestDTO.getDisapproverId();
        this.rejectReason = bcdDisapproveRequestDTO.getRejectReason();
        this.respondDate = bcdDisapproveRequestDTO.getRespondDate();
        this.status = bcdDisapproveRequestDTO.getStatus();
    }

    public void updateCurrentApproverIndex(Integer currentApproverIndex) {
        this.currentApproverIndex = currentApproverIndex;
    }

    // 발주 -> 발주일시 업데이트
    public void updateOrder(Timestamp deletedt) {
        this.orderDate = deletedt;
        this.status = "D";
    }

    public void updateTitle(BcdUpdateRequestDTO updateBcdRequestDTO) {
        this.title = this.title = String.format("[%s]명함신청서(%s)", updateBcdRequestDTO.getTeamNm(), updateBcdRequestDTO.getKorNm());
    }
}