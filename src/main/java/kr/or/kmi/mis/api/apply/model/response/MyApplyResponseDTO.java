package kr.or.kmi.mis.api.apply.model.response;

import kr.or.kmi.mis.api.bcd.model.response.BcdMyResponseDTO;
import kr.or.kmi.mis.api.corpdoc.model.response.CorpDocMyResponseDTO;
import kr.or.kmi.mis.api.doc.model.response.DocMyResponseDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Builder
@Data
@AllArgsConstructor
public class MyApplyResponseDTO {

    private List<BcdMyResponseDTO> myBcdResponses;
    private List<DocMyResponseDTO> myDocResponses;
    private List<CorpDocMyResponseDTO> myCorpDocResponses;

    public static MyApplyResponseDTO of(List<BcdMyResponseDTO> bcdMyResponses, List<DocMyResponseDTO> myDocResponses,
                                        List<CorpDocMyResponseDTO> myCorpDocResponses) {
        return MyApplyResponseDTO.builder()
                .myBcdResponses(bcdMyResponses)
                .myDocResponses(myDocResponses)
                .myCorpDocResponses(myCorpDocResponses)
                .build();
    }
}
