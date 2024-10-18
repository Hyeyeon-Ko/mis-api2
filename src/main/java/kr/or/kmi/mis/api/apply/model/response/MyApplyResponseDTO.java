package kr.or.kmi.mis.api.apply.model.response;

import kr.or.kmi.mis.api.bcd.model.response.BcdMyResponseDTO;
import kr.or.kmi.mis.api.corpdoc.model.response.CorpDocMyResponseDTO;
import kr.or.kmi.mis.api.doc.model.response.DocMyResponseDTO;
import kr.or.kmi.mis.api.seal.model.response.SealMyResponseDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.domain.Page;

@Builder
@Data
@AllArgsConstructor
public class MyApplyResponseDTO {

    private Page<BcdMyResponseDTO> myBcdResponses;
    private Page<DocMyResponseDTO> myDocResponses;
    private Page<CorpDocMyResponseDTO> myCorpDocResponses;
    private Page<SealMyResponseDTO> mySealResponses;

//    private List<BcdMyResponseDTO> myBcdResponses2;
//    private List<DocMyResponseDTO> myDocResponses2;
//    private List<CorpDocMyResponseDTO> myCorpDocResponses2;
//    private List<SealMyResponseDTO> mySealResponses2;

    private Page<Object> pagedResult;

    public static MyApplyResponseDTO of(Page<BcdMyResponseDTO> bcdMyResponses, Page<DocMyResponseDTO> myDocResponses,
                                        Page<CorpDocMyResponseDTO> myCorpDocResponses, Page<SealMyResponseDTO> mySealResponses) {
        return MyApplyResponseDTO.builder()
                .myBcdResponses(bcdMyResponses)
                .myDocResponses(myDocResponses)
                .myCorpDocResponses(myCorpDocResponses)
                .mySealResponses(mySealResponses)
                .build();
    }

    public static MyApplyResponseDTO of(Page<Object> pagedResult) {
        return MyApplyResponseDTO.builder()
                .pagedResult(pagedResult)
                .build();
    }
}
