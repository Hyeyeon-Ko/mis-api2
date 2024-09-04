package kr.or.kmi.mis.api.apply.model.response;

import kr.or.kmi.mis.api.bcd.model.response.BcdMyResponseDTO;
import kr.or.kmi.mis.api.corpdoc.model.response.CorpDocMyResponseDTO;
import kr.or.kmi.mis.api.doc.model.response.DocMyResponseDTO;
import kr.or.kmi.mis.api.seal.model.response.SealMyResponseDTO;
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
    private List<SealMyResponseDTO> mySealResponses;

    public static MyApplyResponseDTO of(List<BcdMyResponseDTO> bcdMyResponses, List<DocMyResponseDTO> myDocResponses,
                                        List<CorpDocMyResponseDTO> myCorpDocResponses, List<SealMyResponseDTO> mySealResponses) {
        return MyApplyResponseDTO.builder()
                .myBcdResponses(bcdMyResponses)
                .myDocResponses(myDocResponses)
                .myCorpDocResponses(myCorpDocResponses)
                .mySealResponses(mySealResponses)
                .build();
    }
}
