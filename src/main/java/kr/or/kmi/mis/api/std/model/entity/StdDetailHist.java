package kr.or.kmi.mis.api.std.model.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.sql.Timestamp;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
@Table(name = "cmm_detail_code_hist")
public class StdDetailHist extends BaseSystemFieldEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "hist_id")
    private Long histId;

    @ManyToOne
    @JoinColumn(name = "detail_cd", nullable = false)
    private StdDetail detailCd;

    @Column(name = "group_cd", length = 20, nullable = false)
    private String groupCd;

    @Column(name = "detail_nm", length = 20, nullable = false)

    private String detailNm;

    @Column(name = "rgstr_id", length = 20)
    private String rgstrId;

    @CreationTimestamp
    @Column(name = "rgst_dt", length = 20)
    private Timestamp rgstDt;

    @Column(name = "updtr_id", length = 20)
    private String updtrId;

    @Column(name = "updt_dt", length = 20)
    private Timestamp updtDt;

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

    @Builder
    public StdDetailHist(StdDetail stdDetail, String rgstrId) {
        this.detailCd = stdDetail;
        this.groupCd = stdDetail.getGroupCd().getGroupCd();
        this.detailNm = stdDetail.getDetailNm();
        this.rgstrId = rgstrId;
        this.rgstDt = stdDetail.getUpdtDt();
        this.etcItem1 = etcItem1;
        this.etcItem2 = etcItem2;
        this.etcItem3 = etcItem3;
        this.etcItem4 = etcItem4;
        this.etcItem5 = etcItem5;
    }
/*
    public void update(String etcDetlCd, String fromDd, String toDd, String lastUpdtr, Timestamp lastUpdtDt,
                       String etcItem1, String etcItem2, String etcItem3, String etcItem4, String etcItem5) {
        this.etcDetlCd = etcDetlCd;
        this.fromDd = fromDd;
        this.toDd = toDd;
        this.lastUpdtr = lastUpdtr;
        this.lastUpdtDt = lastUpdtDt;
        this.etcItem1 = etcItem1;
        this.etcItem2 = etcItem2;
        this.etcItem3 = etcItem3;
        this.etcItem4 = etcItem4;
        this.etcItem5 = etcItem5;
    }*/

}
