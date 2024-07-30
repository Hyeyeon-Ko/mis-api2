package kr.or.kmi.mis.api.std.model.entity;

import jakarta.persistence.*;
import kr.or.kmi.mis.api.std.model.request.StdDetailUpdateRequestDTO;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
@Table(name = "cmm_detail_code")
@IdClass(StdDetailId.class)
public class StdDetail extends BaseSystemFieldEntity{

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

    @Builder
    public StdDetail(String detailCd, StdGroup groupCd, String detailNm, String fromDd, String toDd,
                String etcItem1, String etcItem2, String etcItem3,String etcItem4,String etcItem5, String etcItem6) {
        this.detailCd = detailCd;
        this.groupCd = groupCd;
        this.detailNm = detailNm;
        this.fromDd = fromDd;
        this.toDd = toDd;
        this.useAt = "Y";
        this.etcItem1 = etcItem1;
        this.etcItem2 = etcItem2;
        this.etcItem3 = etcItem3;
        this.etcItem4 = etcItem4;
        this.etcItem5 = etcItem5;
        this.etcItem6 = etcItem6;
    }

    public void update(StdDetailUpdateRequestDTO stdDetailRequestDTO) {
        this.detailCd = stdDetailRequestDTO.getDetailCd();
        this.detailNm = stdDetailRequestDTO.getDetailNm();
        this.fromDd = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        this.toDd = "991231";
        this.etcItem1 = stdDetailRequestDTO.getEtcItem1();
        this.etcItem2 = stdDetailRequestDTO.getEtcItem2();
        this.etcItem3 = stdDetailRequestDTO.getEtcItem3();
        this.etcItem4 = stdDetailRequestDTO.getEtcItem4();
        this.etcItem5 = stdDetailRequestDTO.getEtcItem5();
        this.etcItem6 = stdDetailRequestDTO.getEtcItem6();
    }

    public void updateUseAt(String useAt) {
        this.useAt = useAt;
    }

    public void updateToDd(String toDd) {
        this.toDd = toDd;
    }
}

