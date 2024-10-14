package kr.or.kmi.mis.api.bcd.model.entity;

import jakarta.persistence.*;
import kr.or.kmi.mis.api.bcd.model.request.BcdUpdateRequestDTO;
import lombok.*;

import java.time.LocalDateTime;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@ToString
@Entity
@Table(name = "hrmtbcdd")
public class BcdDetail {

    @Id
    @Column(name = "draft_id", length = 12)
    private String draftId;

    @Column(length = 20)
    private String lastUpdtr;         // 명함 최종 수정자

    private LocalDateTime lastupdtDate;   // 명함 최종 수정일

    @Column(length = 1)
    private String division;          // 명함구분 - A:회사정보, B:영문명함

    @Column(length = 20)
    private String userId;            // 명함 대상자 사번

    @Column(length = 20)
    private String korNm;

    @Column(length = 20)
    private String engNm;

    @Column(length = 100)
    private String instCd;

    @Column(length = 100)
    private String deptCd;

    @Column(length = 100)
    private String teamCd;

    @Column(length = 20)
    private String teamNm;

    @Column(length = 20)
    private String engteamNm;

    @Column(length = 20)
    private String gradeCd;

    @Column(length = 20)
    private String gradeNm;

    @Column(length = 20)
    private String engradeNm;

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
    public BcdDetail(String draftId, String division, String userId, String korNm, String engNm,
                     String instCd, String deptCd, String teamCd, String teamNm, String engteamNm,
                     String gradeCd, String gradeNm, String enGradeNm, String extTel, String faxTel, String phoneTel,
                     String email, String address, String engAddress, Integer quantity) {
        this.draftId = draftId;
        this.division = division;
        this.userId = userId;
        this.korNm = korNm;
        this.engNm = engNm;
        this.instCd = instCd;
        this.deptCd = deptCd;
        this.teamCd = teamCd;
        this.teamNm = teamNm;
        this.engteamNm = engteamNm;
        this.gradeCd = gradeCd;
        this.gradeNm = gradeNm;
        this.engradeNm = enGradeNm;
        this.extTel = extTel;
        this.faxTel = faxTel;
        this.phoneTel = phoneTel;
        this.email = email;
        this.address = address;
        this.engAddress = engAddress;
        this.quantity = quantity;
    }

    public void update(BcdUpdateRequestDTO bcdUpdateRequestDTO, String updtr) {
        this.lastUpdtr = updtr;
        this.division = bcdUpdateRequestDTO.getDivision();
        this.engNm = bcdUpdateRequestDTO.getEngNm();
        this.instCd = bcdUpdateRequestDTO.getInstCd();
        this.deptCd = bcdUpdateRequestDTO.getDeptCd();
        this.teamCd = bcdUpdateRequestDTO.getTeamCd();
        this.teamNm = bcdUpdateRequestDTO.getTeamNm();
        this.engteamNm = bcdUpdateRequestDTO.getEngTeamNm();
        this.gradeCd = bcdUpdateRequestDTO.getGradeCd();
        this.gradeNm = bcdUpdateRequestDTO.getGradeNm();
        this.engradeNm = bcdUpdateRequestDTO.getEnGradeNm();
        this.extTel = bcdUpdateRequestDTO.getExtTel();
        this.faxTel = bcdUpdateRequestDTO.getFaxTel();
        this.phoneTel = bcdUpdateRequestDTO.getPhoneTel();
        this.email = bcdUpdateRequestDTO.getEmail();
        this.address = bcdUpdateRequestDTO.getAddress();
        this.engAddress = bcdUpdateRequestDTO.getEngAddress();
        this.quantity = bcdUpdateRequestDTO.getQuantity();
    }

    @PreUpdate
    public void onPreUpdate() {
        this.lastupdtDate = LocalDateTime.now();
    }
}
