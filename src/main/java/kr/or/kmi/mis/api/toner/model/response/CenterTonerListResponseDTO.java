package kr.or.kmi.mis.api.toner.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
public class CenterTonerListResponseDTO {

    private List<TonerExcelResponseDTO> foundationResponses ;
    private List<TonerExcelResponseDTO> bonwonResponses ;
    private List<TonerExcelResponseDTO> yeouidoResponses ;
    private List<TonerExcelResponseDTO> gangnamResponses ;
    private List<TonerExcelResponseDTO> gwanghwamunResponses ;
    private List<TonerExcelResponseDTO> suwonResponses ;
    private List<TonerExcelResponseDTO> daeguResponses ;
    private List<TonerExcelResponseDTO> busanResponses ;
    private List<TonerExcelResponseDTO> gwangjuResponses ;
    private List<TonerExcelResponseDTO> jejuResponses ;

    public static CenterTonerListResponseDTO of(List<TonerExcelResponseDTO> foundationResponses, List<TonerExcelResponseDTO> bonwonResponses,
                                                 List<TonerExcelResponseDTO> yeouidoResponses, List<TonerExcelResponseDTO> gangnamResponses,
                                                 List<TonerExcelResponseDTO> gwanghwamunResponses, List<TonerExcelResponseDTO> suwonResponses,
                                                 List<TonerExcelResponseDTO> daeguResponses, List<TonerExcelResponseDTO> busanResponses,
                                                 List<TonerExcelResponseDTO> gwangjuResponses, List<TonerExcelResponseDTO> jejuResponses) {
        return CenterTonerListResponseDTO.builder()
                .foundationResponses(foundationResponses)
                .bonwonResponses(bonwonResponses)
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
