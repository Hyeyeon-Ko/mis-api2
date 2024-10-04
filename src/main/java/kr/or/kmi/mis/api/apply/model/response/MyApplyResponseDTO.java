package kr.or.kmi.mis.api.apply.model.response;

import kr.or.kmi.mis.api.bcd.model.response.BcdMyResponseDTO;
import kr.or.kmi.mis.api.corpdoc.model.response.CorpDocMyResponseDTO;
import kr.or.kmi.mis.api.doc.model.response.DocMyResponseDTO;
import kr.or.kmi.mis.api.seal.model.response.SealMyResponseDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.domain.Page;

import java.util.List;

@Builder
@Data
@AllArgsConstructor
public class MyApplyResponseDTO {

    private Page<BcdMyResponseDTO> myBcdResponses;
    private Page<DocMyResponseDTO> myDocResponses;
    private Page<CorpDocMyResponseDTO> myCorpDocResponses;
    private Page<SealMyResponseDTO> mySealResponses;

    private List<BcdMyResponseDTO> myBcdResponses2;
    private List<DocMyResponseDTO> myDocResponses2;
    private List<CorpDocMyResponseDTO> myCorpDocResponses2;
    private List<SealMyResponseDTO> mySealResponses2;

    public static MyApplyResponseDTO of(Page<BcdMyResponseDTO> bcdMyResponses, Page<DocMyResponseDTO> myDocResponses,
                                        Page<CorpDocMyResponseDTO> myCorpDocResponses, Page<SealMyResponseDTO> mySealResponses) {
        return MyApplyResponseDTO.builder()
                .myBcdResponses(bcdMyResponses)
                .myDocResponses(myDocResponses)
                .myCorpDocResponses(myCorpDocResponses)
                .mySealResponses(mySealResponses)
                .build();
    }
    public static MyApplyResponseDTO in(List<BcdMyResponseDTO> bcdMyResponses2, List<DocMyResponseDTO> myDocResponses2,
                                        List<CorpDocMyResponseDTO> myCorpDocResponses2, List<SealMyResponseDTO> mySealResponses2) {
        return MyApplyResponseDTO.builder()
                .myBcdResponses2(bcdMyResponses2)
                .myDocResponses2(myDocResponses2)
                .myCorpDocResponses2(myCorpDocResponses2)
                .mySealResponses2(mySealResponses2)
                .build();
    }
}
