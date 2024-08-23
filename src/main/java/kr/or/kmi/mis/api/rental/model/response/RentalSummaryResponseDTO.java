package kr.or.kmi.mis.api.rental.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class RentalSummaryResponseDTO {

    private String center;
    private int waterPurifier;
    private int airPurifier;
    private int bidet;
    private int monthlyRentalFee;
}
