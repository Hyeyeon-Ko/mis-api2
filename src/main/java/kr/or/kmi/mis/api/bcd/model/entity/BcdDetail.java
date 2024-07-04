package kr.or.kmi.mis.api.bcd.model.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.sql.Timestamp;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@ToString
@Entity
@Table(name = "hrmtbcdd")
@IdClass(BcdDetail.class)
public class BcdDetail {

    @Id
    private Long draftId;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long seqId;

    @Column(nullable = false, length = 20)
    private String drafter;        // 명함 기안자

    @Column(length = 20)
    private String drafterId;

    @Column(nullable = false)
    private Timestamp draftDate;

    @Column(length = 20)
    private String lastUpdtId;     // 명함 최종 수정자

    @CreationTimestamp
    @Column(insertable = false)
    private Timestamp lastUpdtDate;

    @Column(length = 20)
    private String userId;       // 명함 대상자 사번

    @Column(length = 1)
    private String division;  // 명함구분 - A:회사정보, B:영문명함

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

    @Column(length = 20)
    private String grade;

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

    private Integer quantity;

    @Builder
    public BcdDetail(Long draftId, String drafter, String drafterId, Timestamp draftDate, String userId, String division,
                     String korNm, String engNm, String instNm, String deptNm, String teamNm, String grade,
                     String extTel, String faxTel, String phoneTel, String email, String address, Integer quantity) {
        this.draftId = draftId;
        this.drafter = drafter;
        this.drafterId = drafterId;
        this.draftDate = draftDate;
        this.userId = userId;
        this.division = division;
        this.korNm = korNm;
        this.engNm = engNm;
        this.instNm = instNm;
        this.deptNm = deptNm;
        this.teamNm = teamNm;
        this.grade = grade;
        this.extTel = extTel;
        this.faxTel = faxTel;
        this.phoneTel = phoneTel;
        this.email = email;
        this.address = address;
        this.quantity = quantity;
    }

    public void update(String lastUpdtId, Timestamp lastUpdtDt) {
        this.lastUpdtId = lastUpdtId;
        this.lastUpdtDate = lastUpdtDt;
    }

}
