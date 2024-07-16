package kr.or.kmi.mis.api.std.model.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UpdateTimestamp;

import java.sql.Timestamp;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
@Table(name = "cmm_detail_code")
public class StdDetail extends BaseSystemFieldEntity{

    @Id
    @Column(name = "detail_cd", length = 20)
    private String detailCd;

    @ManyToOne
    @JoinColumn(name = "group_cd", nullable = false)
    private StdGroup groupCd;

    @Column(name = "detail_nm", length = 20, nullable = false)
    private String detailNm;

    @Column(name = "updtr_id", length = 20)
    private String updtrId;

    @UpdateTimestamp
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
    public StdDetail(String detailCd, StdGroup groupCd, String detailNm, String updtrId,
                String etcItem1, String etcItem2, String etcItem3,String etcItem4,String etcItem5, String etcItem6) {
        this.detailCd = detailCd;
        this.groupCd = groupCd;
        this.detailNm = detailNm;
        this.updtrId = updtrId;
        this.etcItem1 = etcItem1;
        this.etcItem2 = etcItem2;
        this.etcItem3 = etcItem3;
        this.etcItem4 = etcItem4;
        this.etcItem5 = etcItem5;
    }
}

