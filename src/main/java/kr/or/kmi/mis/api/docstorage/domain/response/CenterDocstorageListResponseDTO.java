package kr.or.kmi.mis.api.docstorage.domain.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
public class CenterDocstorageListResponseDTO {

    private List<DocstorageResponseDTO> foundationResponses ;
    private List<DocstorageResponseDTO> gwanghwamunResponses ;
    private List<DocstorageResponseDTO> yeouidoResponses ;
    private List<DocstorageResponseDTO> gangnamResponses ;
    private List<DocstorageResponseDTO> suwonResponses ;
    private List<DocstorageResponseDTO> daeguResponses ;
    private List<DocstorageResponseDTO> busanResponses ;
    private List<DocstorageResponseDTO> gwangjuResponses ;
    private List<DocstorageResponseDTO> jejuResponses ;

    public static CenterDocstorageListResponseDTO of(List<DocstorageResponseDTO> foundationResponses, List<DocstorageResponseDTO> gwanghwamunResponses,
                                                     List<DocstorageResponseDTO> yeouidoResponses, List<DocstorageResponseDTO> gangnamResponses,
                                                     List<DocstorageResponseDTO> suwonResponses, List<DocstorageResponseDTO> daeguResponses,
                                                     List<DocstorageResponseDTO> busanResponses, List<DocstorageResponseDTO> gwangjuResponses,
                                                     List<DocstorageResponseDTO> jejuResponses) {
        return CenterDocstorageListResponseDTO.builder()
                .foundationResponses(foundationResponses)
                .gwanghwamunResponses(gwanghwamunResponses)
                .yeouidoResponses(yeouidoResponses)
                .gangnamResponses(gangnamResponses)
                .suwonResponses(suwonResponses)
                .daeguResponses(daeguResponses)
                .busanResponses(busanResponses)
                .gwangjuResponses(gwangjuResponses)
                .jejuResponses(jejuResponses)
                .build();
    }

}
