package kr.or.kmi.mis.api.apply.model.response;

import kr.or.kmi.mis.api.bcd.model.response.BcdPendingResponseDTO;
import kr.or.kmi.mis.api.corpdoc.model.response.CorpDocPendingResponseDTO;
import kr.or.kmi.mis.api.doc.model.response.DocPendingResponseDTO;
import kr.or.kmi.mis.api.seal.model.response.SealPendingResponseDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.domain.Page;

@Builder
@Data
@AllArgsConstructor
public class PendingResponseDTO {

    /**
     * 전체 승인대기 신청 목록 반환 DTO
     * 해당 DTO 에 한번에 담아서 반환
     * */

    private Page<BcdPendingResponseDTO> bcdPendingResponses;
    private Page<DocPendingResponseDTO> docPendingResponses;
    private Page<CorpDocPendingResponseDTO> corpDocPendingResponses;
    private Page<SealPendingResponseDTO> sealPendingResponses;

    public static PendingResponseDTO of(Page<BcdPendingResponseDTO> bcdPendingResponses, Page<DocPendingResponseDTO> docPendingResponses,
                                        Page<CorpDocPendingResponseDTO> corpDocPendingResponses, Page<SealPendingResponseDTO> sealPendingResponses) {
        return PendingResponseDTO.builder()
                .bcdPendingResponses(bcdPendingResponses)
                .docPendingResponses(docPendingResponses)
                .corpDocPendingResponses(corpDocPendingResponses)
                .sealPendingResponses(sealPendingResponses)
                .build();
    }

}
