package kr.or.kmi.mis.api.bcd.model.entity;

import jakarta.persistence.*;
import kr.or.kmi.mis.api.bcd.model.request.BcdUpdateRequestDTO;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.sql.Timestamp;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@ToString
@Entity
@Table(name = "hrmtbcdd_hist")
@IdClass(DraftSeqPK.class)
public class BcdHistory {

    @Id
    @Column(name = "draft_id")
    private Long draftId;

    @Id
    @Column(name = "seq_id")
    private Long seqId;

    @Column(length = 20)
    private String updtr;        // 명함 수정자

    @CreationTimestamp
    @Column(length = 20)
    private Timestamp updtDate;     // 명함 수정일자

    @Column(length = 1)
    private String division;  // 명함구분 - A:회사정보, B:영문명함

    @Column(length = 20)
    private String engNm;

    @Column(length = 100)
    private String instNm;

    @Column(length = 100)
    private String deptNm;

    @Column(length = 100)
    private String teamNm;

    @Column(length = 50)
    private String engTeamnm;

    @Column(length = 20)
    private String grade;

    @Column(length = 20)
    private String engGrade;

    @Column(length = 20)
    private String extTel;

    @Column(length = 20)
    private String faxTel;

    @Column(length = 20)
    private String phoneTel;

    @Column(length = 50)
    private String email;

    @Column(length = 200)
    private String address;

    @Column(length = 200)
    private String engAddress;

    private Integer quantity;

    @Builder
    public BcdHistory(BcdDetail bcdDetail, Long seqId,
                      String instNm, String deptNm, String teamNm, String engTeamnm, String grade, String engGrade) {
        this.seqId = seqId;
        this.draftId = bcdDetail.getDraftId();
        this.updtr = bcdDetail.getLastUpdtr();
        this.division = bcdDetail.getDivision();
        this.engNm = bcdDetail.getEngNm();
        this.instNm = instNm;
        this.deptNm = deptNm;
        this.teamNm = teamNm;
        this.engTeamnm = engTeamnm;
        this.grade = grade;
        this.engGrade = engGrade;
        this.extTel = bcdDetail.getExtTel();
        this.faxTel = bcdDetail.getFaxTel();
        this.phoneTel = bcdDetail.getPhoneTel();
        this.email = bcdDetail.getEmail();
        this.address = bcdDetail.getAddress();
        this.engAddress = bcdDetail.getEngAddress();
        this.quantity = bcdDetail.getQuantity();
    }
}