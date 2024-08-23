package kr.or.kmi.mis.api.rental.model.response;

import kr.or.kmi.mis.api.docstorage.domain.response.CenterResponseDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
public class RentalTotalListResponseDTO {

    private List<CenterResponseDTO> centerResponses;
    private List<CenterRentalListResponseDTO> centerRentalResponses;
    private List<RentalSummaryResponseDTO> summaryResponses;

    public static RentalTotalListResponseDTO of(List<CenterResponseDTO> centerResponses, List<CenterRentalListResponseDTO> centerRentalResponses,
                                                List<RentalSummaryResponseDTO> summaryResponses) {
        return RentalTotalListResponseDTO.builder()
                .centerResponses(centerResponses)
                .centerRentalResponses(centerRentalResponses)
                .summaryResponses(summaryResponses)
                .build();
    }
}
