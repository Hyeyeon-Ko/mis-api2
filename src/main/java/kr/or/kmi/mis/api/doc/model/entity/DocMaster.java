package kr.or.kmi.mis.api.doc.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import kr.or.kmi.mis.cmm.model.entity.BaseSystemFieldEntity;
import lombok.*;

import java.time.LocalDateTime;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@ToString
@Entity
@Table(name = "hrmtdocm")
public class DocMaster extends BaseSystemFieldEntity {

    @Id
    @Column(nullable = false, length = 12)
    private String draftId;

    @Column(nullable = false)
    private LocalDateTime draftDate;

    private LocalDateTime respondDate;

    private LocalDateTime endDate;

    @Column(nullable = false, length = 20)
    private String drafter;

    @Column(length = 20)
    private String drafterId;

    @Column(length = 20)
    private String approver;

    @Column(length = 20)
    private String approverId;

    @Column(length = 500)
    private String title;

    @Column(length = 1000)
    private String approverChain;

    @Column(nullable = false)
    private Integer currentApproverIndex;

    @Column(length = 1)
    private String status;

    @Column(length = 20)
    private String instCd;

    @Column(length = 20)
    private String deptCd;

    public void confirm(String status, String approver, String approverId) {
        this.status = status;
        this.respondDate = LocalDateTime.now();
        this.approver = approver;
        this.approverId = approverId;
    }

    public void delete(String status) {
        this.status = status;
        this.respondDate = LocalDateTime.now();
    }

    @Builder
    public DocMaster(String draftId, String title, LocalDateTime draftDate, String drafter, String drafterId,
                     String approverChain, String status, String instCd, String deptCd) {
        this.draftId = draftId;
        this.title = title;
        this.draftDate = draftDate;
        this.drafter = drafter;
        this.drafterId = drafterId;
        this.approverChain = approverChain;
        this.currentApproverIndex = 0;
        this.status = status;
        this.instCd = instCd;
        this.deptCd = deptCd;
    }

    public void updateApproverChain(String approverChain) {
        this.approverChain = approverChain;
        this.currentApproverIndex = 0;
    }

    public String getCurrentApproverId() {
        return this.approverChain.split(", ")[this.currentApproverIndex];
    }

    public void updateCurrentApproverIndex(Integer currentApproverIndex) {
        this.currentApproverIndex = currentApproverIndex + 1;
    }

    public void updateStatus(String status) {
        this.status = status;
    }

    public void updateRespondDate(LocalDateTime respondDate) {
        this.respondDate = respondDate;
    }
}
