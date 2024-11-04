package kr.or.kmi.mis.api.toner.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import kr.or.kmi.mis.api.toner.model.request.TonerInfoRequestDTO;
import kr.or.kmi.mis.cmm.model.entity.BaseSystemFieldEntity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
@Table(name = "hrmtptmd")
public class TonerInfo extends BaseSystemFieldEntity {

    @Id
    @Column(name = "mng_num", nullable = false)
    private String mngNum;

    @Column(name = "floor")
    private String floor;

    @Column(name = "team_nm", length = 20)
    private String teamNm;

    @Column(name = "manager", length = 10)
    private String manager;

    @Column(name = "sub_manager", length = 10)
    private String subManager;

    @Column(name = "location", length = 50)
    private String location;

    @Column(name = "product_nm", length = 10)
    private String productNm;

    @Column(name = "model_nm", length = 50)
    private String modelNm;

    @Column(name = "sn", length = 50)
    private String sn;

    @Column(name = "company", length = 20)
    private String company;

    @Column(name = "manuDate", length = 10)
    private String manuDate;

    @Column(name = "toner_nm", length = 50)
    private String tonerNm;

    @Column(name = "inst_cd", length = 20)
    private String instCd;

    @Builder
    public TonerInfo(String mngNum, String floor, String teamNm, String manager, String subManager,
                     String location, String productNm, String modelNm, String sn, String company,
                     String manuDate, String tonerNm, String instCd) {
        this.mngNum = mngNum;
        this.floor = floor;
        this.teamNm = teamNm;
        this.manager = manager;
        this.subManager = subManager;
        this.location = location;
        this.productNm = productNm;
        this.modelNm = modelNm;
        this.sn = sn;
        this.company = company;
        this.manuDate = manuDate;
        this.tonerNm = tonerNm;
        this.instCd = instCd;
    }

    public void tonerInfoUpdate(TonerInfoRequestDTO tonerInfoRequestDTO) {
        this.mngNum = tonerInfoRequestDTO.getMngNum();
        this.floor = tonerInfoRequestDTO.getFloor();
        this.teamNm = tonerInfoRequestDTO.getTeamNm();
        this.manager = tonerInfoRequestDTO.getManager();
        this.subManager = tonerInfoRequestDTO.getSubManager();
        this.location = tonerInfoRequestDTO.getLocation();
        this.productNm = tonerInfoRequestDTO.getProductNm();
        this.modelNm = tonerInfoRequestDTO.getModelNm();
        this.sn = tonerInfoRequestDTO.getSn();
        this.company = tonerInfoRequestDTO.getCompany();
        this.manuDate = tonerInfoRequestDTO.getManuDate();
        this.tonerNm = tonerInfoRequestDTO.getTonerNm();
    }
}
