package kr.or.kmi.mis.api.toner.model.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
@Table(name = "hrmtptnd")
@IdClass(TonerDetailId.class)
public class TonerDetail {

    @Id
    private Long itemId;

    @Id
    private String draftId;

    @Column(name = "mng_num", length = 10)
    private String mngNum;

    @Column(name = "team_nm", length = 20)
    private String teamNm;

    @Column(name = "location", length = 50)
    private String location;

    @Column(name = "print_nm", length = 100)
    private String printNm;

    @Column(name = "toner_nm", length = 100)
    private String tonerNm;

    @Column(name = "quantity")
    private int quantity;

    @Column(name = "total_price", length = 20)
    private String totalPrice;

    @Builder
    public TonerDetail(Long itemId, String draftId, String mngNum, String teamNm,
                       String location, String printNm, String tonerNm, int quantity, String totalPrice) {
        this.itemId = itemId;
        this.draftId = draftId;
        this.mngNum = mngNum;
        this.teamNm = teamNm;
        this.location = location;
        this.printNm = printNm;
        this.tonerNm = tonerNm;
        this.quantity = quantity;
        this.totalPrice = totalPrice;
    }


}
