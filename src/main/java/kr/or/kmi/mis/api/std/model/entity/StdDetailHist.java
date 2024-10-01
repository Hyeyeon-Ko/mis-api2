package kr.or.kmi.mis.api.std.model.entity;

import jakarta.persistence.*;
import kr.or.kmi.mis.cmm.model.entity.BaseSystemFieldEntity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
@Table(name = "cmm_detail_code_hist")
public class StdDetailHist extends BaseSystemFieldEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "hist_id")
    private Long histId;

    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "detail_cd", referencedColumnName = "detail_cd"),
            @JoinColumn(name = "group_cd", referencedColumnName = "group_cd"),
    })
    private StdDetail detailCd;

    @Column(name = "detail_nm", length = 20, nullable = false)
    private String detailNm;

    @Column(name = "from_dd", length = 20)
    private String fromDd;

    @Column(name = "to_dd", length = 20)
    private String toDd;

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
    public StdDetailHist(StdDetail stdDetail) {
        this.detailCd = stdDetail;
        this.detailNm = stdDetail.getDetailNm();
        this.fromDd = stdDetail.getFromDd();
        this.toDd = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        this.etcItem1 = stdDetail.getEtcItem1();
        this.etcItem2 = stdDetail.getEtcItem2();
        this.etcItem3 = stdDetail.getEtcItem3();
        this.etcItem4 = stdDetail.getEtcItem4();
        this.etcItem5 = stdDetail.getEtcItem5();
        this.etcItem6 = stdDetail.getEtcItem6();
        this.etcItem7 = stdDetail.getEtcItem7();
        this.etcItem8 = stdDetail.getEtcItem8();
        this.etcItem9 = stdDetail.getEtcItem9();
        this.etcItem10 = stdDetail.getEtcItem10();
        this.etcItem11 = stdDetail.getEtcItem11();
    }

}
