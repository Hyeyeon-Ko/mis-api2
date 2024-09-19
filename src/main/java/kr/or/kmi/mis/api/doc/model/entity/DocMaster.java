package kr.or.kmi.mis.api.doc.model.entity;

import jakarta.persistence.*;
import kr.or.kmi.mis.cmm.model.entity.BaseSystemFieldEntity;
import lombok.*;

import java.sql.Timestamp;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@ToString
@Entity
@Table(name = "hrmtdocm")
public class DocMaster extends BaseSystemFieldEntity {

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
        this.respondDate = new Timestamp(System.currentTimeMillis());
        this.approver = approver;
        this.approverId = approverId;
    }

    public void delete(String status) {
        this.status = status;
        this.respondDate = new Timestamp(System.currentTimeMillis());
    }

    @Builder
    public DocMaster(Timestamp draftDate, String drafter, String drafterId, String approverChain, String status, String instCd, String deptCd) {
        this.title =  String.format("문서수발신 신청서 (%s)", drafter);
        this.draftDate = draftDate;
        this.drafter = drafter;
        this.drafterId = drafterId;
        this.approverChain = approverChain;
        this.currentApproverIndex = 0;
        this.status = status;
        this.instCd = instCd;
        this.deptCd = deptCd;
    }

    public String getCurrentApproverId() {
        return this.approverChain.split(", ")[this.currentApproverIndex];
    }

    public void updateCurrentApproverIndex(Integer currentApproverIndex) {
        this.currentApproverIndex = currentApproverIndex;
    }

    public void updateStatus(String status) {
        this.status = status;
    }
}
