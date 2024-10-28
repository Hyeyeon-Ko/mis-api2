package kr.or.kmi.mis.api.bcd.model.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@ToString
@Entity
@Table(name = "hrmtbcdd_hist")
@IdClass(DraftSeqPK.class)
public class BcdHistory {

    @Id
    @Column(name = "draft_id")
    private String draftId;

    @Id
    @Column(name = "seq_id")
    private Long seqId;

    @Column(length = 20)
    private String updtr;        // 명함 수정자

    @CreationTimestamp
    @Column(length = 20)
    private LocalDateTime updtDate;     // 명함 수정일자

    @Column(length = 20)
    private String userId;

    @Column(length = 20)
    private String korNm;

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

    @Column(length = 1)
    private String division;

    private Integer quantity;

    @Builder
    public BcdHistory(BcdDetail bcdDetail, Long seqId,
                      String instNm, String deptNm, String teamNm, String engTeamnm, String grade, String engGrade) {
        this.seqId = seqId;
        this.draftId = bcdDetail.getDraftId();
        this.updtr = bcdDetail.getLastUpdtr();
        this.userId = bcdDetail.getUserId();
        this.korNm = bcdDetail.getKorNm();
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
        this.division = bcdDetail.getDivision();
        this.quantity = bcdDetail.getQuantity();
    }
}