package kr.or.kmi.mis.api.toner.model.entity;

import jakarta.persistence.*;
import kr.or.kmi.mis.api.toner.model.request.TonerDetailDTO;
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

    @Column(name = "toner_nm", length = 500)
    private String tonerNm;

    @Column(name = "price", length = 20)
    private String price;

    @Column(name = "quantity")
    private int quantity;

    @Column(name = "total_price", length = 20)
    private String totalPrice;

    @Column(name = "holding", length = 1)
    private String holding;

    @Builder
    public TonerDetail(Long itemId, String draftId, String mngNum, String teamNm, String location,
                       String printNm, String tonerNm, String price, int quantity, String totalPrice, String holding) {
        this.itemId = itemId;
        this.draftId = draftId;
        this.mngNum = mngNum;
        this.teamNm = teamNm;
        this.location = location;
        this.printNm = printNm;
        this.tonerNm = tonerNm;
        this.price = price;
        this.quantity = quantity;
        this.totalPrice = totalPrice;
        this.holding = holding;
    }

    public int getUnitPrice() {
        String cleanedTotalPrice = totalPrice.replace(",", "");
        int totalPriceInteger = Integer.parseInt(cleanedTotalPrice);
        return totalPriceInteger / quantity;
    }

    public void tonerDetailUpdate(TonerDetailDTO tonerDetailDTO) {
        this.mngNum = tonerDetailDTO.getMngNum();
        this.teamNm = tonerDetailDTO.getTeamNm();
        this.location = tonerDetailDTO.getLocation();
        this.printNm = tonerDetailDTO.getPrintNm();
        this.tonerNm = tonerDetailDTO.getTonerNm();
        this.price = tonerDetailDTO.getPrice();
        this.quantity = tonerDetailDTO.getQuantity();
        this.totalPrice = tonerDetailDTO.getTotalPrice();
    }
}
