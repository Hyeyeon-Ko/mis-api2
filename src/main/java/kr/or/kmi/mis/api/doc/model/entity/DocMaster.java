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

    @Column(length = 1)
    private String status;

    @Column(length = 20)
    private String instCd;

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

    public void revert(String status) {
        this.status = status;
        this.respondDate = new Timestamp(System.currentTimeMillis());
    }

    @Builder
    public DocMaster(Timestamp draftDate, String drafter, String drafterId, String status, String instCd) {
        this.title =  String.format("문서수발신 신청서 (%s)", drafter);
        this.draftDate = draftDate;
        this.drafter = drafter;
        this.drafterId = drafterId;
        this.status = status;
        this.instCd = instCd;
    }

    public void updateStatus(String status) {
        this.status = status;
    }
}
