package kr.or.kmi.mis.api.bcd.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import kr.or.kmi.mis.api.bcd.model.request.BcdUpdateRequestDTO;
import kr.or.kmi.mis.api.confirm.model.request.BcdApproveRequestDTO;
import kr.or.kmi.mis.api.confirm.model.request.BcdDisapproveRequestDTO;
import kr.or.kmi.mis.cmm.model.entity.BaseSystemFieldEntity;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@ToString
@Entity
@Table(name = "hrmtbcdm")
public class BcdMaster extends BaseSystemFieldEntity {

    @Id
    @Column(name = "draft_id", length = 12)
    private String draftId;

    @CreationTimestamp
    @Column(nullable = false)
    private LocalDateTime draftDate;

    private LocalDateTime respondDate;

    private LocalDateTime orderDate;

    private LocalDateTime endDate;

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
    public BcdMaster(String draftId, String drafterId, String drafter, String teamNm, String korNm, String status) {
        this.draftId = draftId;
        this.title = String.format("[%s]명함신청서(%s)", teamNm, korNm);
        this.drafterId = drafterId;
        this.drafter = drafter;
        this.status = status;
    }

    public void updateEndDate(LocalDateTime endDate) {
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

    // 발주 -> 발주일시 업데이트
    public void updateOrder(LocalDateTime deletedt) {
        this.orderDate = deletedt;
        this.status = "D";
    }

    public void updateTitle(BcdUpdateRequestDTO updateBcdRequestDTO) {
        this.title = String.format("[%s]명함신청서(%s)", updateBcdRequestDTO.getTeamNm(), updateBcdRequestDTO.getKorNm());
    }

    public void updateStatus() {
        this.status = "E";
    }

    public void updateRespondDate(LocalDateTime ResponseDate) {
        this.respondDate = ResponseDate;
    }
}