package kr.or.kmi.mis.api.apply.model.response;

import kr.or.kmi.mis.api.bcd.model.response.BcdMasterResponseDTO;
import kr.or.kmi.mis.api.doc.model.response.DocMasterResponseDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
public class ApplyListResponseDTO {

    private List<BcdMasterResponseDTO> bcdMasterResponses;
    private List<DocMasterResponseDTO> docMasterResponses;

    public static ApplyListResponseDTO of(List<BcdMasterResponseDTO> bcdMasterResponses,
                                          List<DocMasterResponseDTO> docMasterResponses) {
        return ApplyListResponseDTO.builder()
                .bcdMasterResponses(bcdMasterResponses)
                .docMasterResponses(docMasterResponses)
                .build();
    }
}
