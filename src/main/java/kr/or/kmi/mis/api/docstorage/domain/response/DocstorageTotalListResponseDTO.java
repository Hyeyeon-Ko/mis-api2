package kr.or.kmi.mis.api.docstorage.domain.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
public class DocstorageTotalListResponseDTO {

    private List<CenterResponseDTO> centerResponses;
    private List<CenterDocstorageListResponseDTO> centerDocstorageResponses;

    public static DocstorageTotalListResponseDTO of(List<CenterResponseDTO> centerResponses, List<CenterDocstorageListResponseDTO> centerDocstorageResponses) {
        return DocstorageTotalListResponseDTO.builder()
                .centerResponses(centerResponses)
                .centerDocstorageResponses(centerDocstorageResponses)
                .build();
    }
}
