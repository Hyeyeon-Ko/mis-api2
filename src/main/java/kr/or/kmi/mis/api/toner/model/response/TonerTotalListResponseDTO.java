package kr.or.kmi.mis.api.toner.model.response;

import kr.or.kmi.mis.api.docstorage.domain.response.CenterResponseDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
public class TonerTotalListResponseDTO {

    private List<CenterResponseDTO> centerResponses;
    private List<CenterTonerListResponseDTO> centerTonerResponses;

    public static TonerTotalListResponseDTO of(List<CenterResponseDTO> centerResponses, List<CenterTonerListResponseDTO> centerTonerResponses) {
        return TonerTotalListResponseDTO.builder()
                .centerResponses(centerResponses)
                .centerTonerResponses(centerTonerResponses)
                .build();
    }
}
