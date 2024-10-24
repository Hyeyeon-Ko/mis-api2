package kr.or.kmi.mis.api.toner.model.response;

import lombok.Builder;
import lombok.Getter;

@Getter
public class TonerPendingResponseDTO {

    private String draftId;
    private String drafter;
    private String draftDate;
    private String mngNum;
    private String teamNm;
    private String printNm;
    private String tonerNm;
    private String quantity;
    private String totalPrice;
    private String holding;

    @Builder
    public TonerPendingResponseDTO(String draftId, String drafter, String draftDate, String mngNum,
                                   String teamNm, String printNm, String tonerNm,
                                   String quantity, String totalPrice, String holding) {
        this.draftId = draftId;
        this.drafter = drafter;
        this.draftDate = draftDate;
        this.mngNum = mngNum;
        this.teamNm = teamNm;
        this.printNm = printNm;
        this.tonerNm = tonerNm;
        this.quantity = quantity;
        this.totalPrice = totalPrice;
        this.holding = holding;
    }
}
