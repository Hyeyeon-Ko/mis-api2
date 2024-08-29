package kr.or.kmi.mis.api.apply.model.response;

import kr.or.kmi.mis.api.bcd.model.response.BcdMasterResponseDTO;
import kr.or.kmi.mis.api.doc.model.response.DocMasterResponseDTO;
import kr.or.kmi.mis.api.seal.model.response.SealMasterResponseDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Builder
@Data
@AllArgsConstructor
public class ApplyResponseDTO {

/**
     * 전체 신청 목록 반환 DTO
     * 명함신청 이외에 다른 신청 (ex. 문서 수발신) 이 있을 경우
     * 해당 DTO 에 한번에 담아서 반환
 * */

    private List<BcdMasterResponseDTO> bcdMasterResponses;
    private List<DocMasterResponseDTO> docMasterResponses;
    private List<SealMasterResponseDTO> sealMasterResponses;

    public static ApplyResponseDTO of(List<BcdMasterResponseDTO> bcdMasterResponses,
                                      List<DocMasterResponseDTO> docMasterResponses,
                                      List<SealMasterResponseDTO> sealMasterResponses) {
        return ApplyResponseDTO.builder()
                .bcdMasterResponses(bcdMasterResponses)
                .docMasterResponses(docMasterResponses)
                .sealMasterResponses(sealMasterResponses)
                .build();
    }

}
