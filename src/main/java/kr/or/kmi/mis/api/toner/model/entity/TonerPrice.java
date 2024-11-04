package kr.or.kmi.mis.api.toner.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import kr.or.kmi.mis.api.toner.model.request.TonerPriceRequestDTO;
import kr.or.kmi.mis.cmm.model.entity.BaseSystemFieldEntity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
@Table(name = "hrmtptpd")
public class TonerPrice extends BaseSystemFieldEntity {

    @Id
    @Column(name = "toner_nm", nullable = false)
    private String tonerNm;

    @Column(name = "model_nm")
    private String modelNm;

    @Column(name = "company", length = 20)
    private String company;

    @Column(name = "division", length = 1)
    private String division;

    @Column(name = "price")
    private String price;

    @Column(name = "special_note", length = 100)
    private String specialNote;

    @Builder
    public TonerPrice(String tonerNm, String modelNm, String company,
                      String division, String price, String specialNote) {
        this.tonerNm = tonerNm;
        this.modelNm = modelNm;
        this.company = company;
        this.division = division;
        this.price = price;
        this.specialNote = specialNote;
    }

    public void tonerPriceUpdate(TonerPriceRequestDTO tonerPriceRequestDTO) {
        this.tonerNm = tonerPriceRequestDTO.getTonerNm();
        this.modelNm = tonerPriceRequestDTO.getModelNm();
        this.company = tonerPriceRequestDTO.getCompany();
        this.division = tonerPriceRequestDTO.getDivision();
        this.price = tonerPriceRequestDTO.getPrice();
        this.specialNote = tonerPriceRequestDTO.getSpecialNote();

    }
}
