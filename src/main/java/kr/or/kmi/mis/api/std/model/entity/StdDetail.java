package kr.or.kmi.mis.api.std.model.entity;

import jakarta.persistence.*;
import kr.or.kmi.mis.api.std.model.request.StdDetailUpdateRequestDTO;
import kr.or.kmi.mis.cmm.model.entity.BaseSystemFieldEntity;
import lombok.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
@Table(name = "cmm_detail_code")
@IdClass(StdDetailId.class)
public class StdDetail extends BaseSystemFieldEntity {

    @Id
    @Column(name = "detail_cd", length = 20)
    private String detailCd;

    @Id
    @ManyToOne
    @JoinColumn(name = "group_cd", nullable = false)
    private StdGroup groupCd;

    @Column(name = "detail_nm", length = 20, nullable = false)
    private String detailNm;

    @Column(name = "from_dd", length = 20)
    private String fromDd;

    @Column(name = "to_dd", length = 20)
    private String toDd;

    @Column(name = "use_at", length = 1)
    private String useAt;

    @Column(name = "etc_item1", length = 100)
    private String etcItem1;

    @Column(name = "etc_item2", length = 100)
    private String etcItem2;

    @Column(name = "etc_item3", length = 100)
    private String etcItem3;

    @Column(name = "etc_item4", length = 100)
    private String etcItem4;

    @Column(name = "etc_item5", length = 100)
    private String etcItem5;

    @Column(name = "etc_item6", length = 100)
    private String etcItem6;

    @Column(name = "etc_item7", length = 100)
    private String etcItem7;

    @Column(name = "etc_item8", length = 100)
    private String etcItem8;

    @Column(name = "etc_item9", length = 100)
    private String etcItem9;

    @Column(name = "etc_item10", length = 100)
    private String etcItem10;

    @Column(name = "etc_item11", length = 100)
    private String etcItem11;

    @Builder
    public StdDetail(String detailCd, StdGroup groupCd, String detailNm,
                     String etcItem1, String etcItem2, String etcItem3, String etcItem4, String etcItem5,
                     String etcItem6, String etcItem7, String etcItem8, String etcItem9, String etcItem10, String etcItem11) {
        this.detailCd = detailCd;
        this.groupCd = groupCd;
        this.detailNm = detailNm;
        this.fromDd = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        this.toDd = "991231";
        this.useAt = "Y";
        this.etcItem1 = etcItem1;
        this.etcItem2 = etcItem2;
        this.etcItem3 = etcItem3;
        this.etcItem4 = etcItem4;
        this.etcItem5 = etcItem5;
        this.etcItem6 = etcItem6;
        this.etcItem7 = etcItem7;
        this.etcItem8 = etcItem8;
        this.etcItem9 = etcItem9;
        this.etcItem10 = etcItem10;
        this.etcItem11 = etcItem11;
    }

    public void update(StdDetailUpdateRequestDTO stdDetailRequestDTO) {
        this.detailCd = stdDetailRequestDTO.getDetailCd();
        this.detailNm = stdDetailRequestDTO.getDetailNm();
        this.fromDd = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        this.toDd = "991231";
        this.etcItem1 = stdDetailRequestDTO.getEtcItem1();
        this.etcItem2 = stdDetailRequestDTO.getEtcItem2();
        this.etcItem3 = stdDetailRequestDTO.getEtcItem3();
        this.etcItem4 = stdDetailRequestDTO.getEtcItem4();
        this.etcItem5 = stdDetailRequestDTO.getEtcItem5();
        this.etcItem6 = stdDetailRequestDTO.getEtcItem6();
        this.etcItem7 = stdDetailRequestDTO.getEtcItem7();
        this.etcItem8 = stdDetailRequestDTO.getEtcItem8();
        this.etcItem9 = stdDetailRequestDTO.getEtcItem9();
        this.etcItem10 = stdDetailRequestDTO.getEtcItem10();
        this.etcItem11 = stdDetailRequestDTO.getEtcItem11();
    }

    public void updateUseAt(String useAt) {
        this.useAt = useAt;
    }

    public void updateToDd(String toDd) {
        this.toDd = toDd;
    }

    public void updateEtcItem3(String etcItem3) {
        this.etcItem3 = etcItem3;
    }
}

