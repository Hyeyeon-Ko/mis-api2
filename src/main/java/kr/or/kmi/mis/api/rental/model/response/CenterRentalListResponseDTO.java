package kr.or.kmi.mis.api.rental.model.response;

import kr.or.kmi.mis.api.docstorage.domain.response.CenterDocstorageListResponseDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
public class CenterRentalListResponseDTO {

    private List<RentalResponseDTO> foundationResponses ;
    private List<RentalResponseDTO> bonwonResponses ;
    private List<RentalResponseDTO> yeouidoResponses ;
    private List<RentalResponseDTO> gangnamResponses ;
    private List<RentalResponseDTO> gwanghwamunResponses ;
    private List<RentalResponseDTO> suwonResponses ;
    private List<RentalResponseDTO> daeguResponses ;
    private List<RentalResponseDTO> busanResponses ;
    private List<RentalResponseDTO> gwangjuResponses ;
    private List<RentalResponseDTO> jejuResponses ;

    public static CenterRentalListResponseDTO of(List<RentalResponseDTO> foundationResponses, List<RentalResponseDTO> bonwonResponses,
                                                 List<RentalResponseDTO> yeouidoResponses, List<RentalResponseDTO> gangnamResponses,
                                                 List<RentalResponseDTO> gwanghwamunResponses, List<RentalResponseDTO> suwonResponses,
                                                 List<RentalResponseDTO> daeguResponses, List<RentalResponseDTO> busanResponses,
                                                 List<RentalResponseDTO> gwangjuResponses, List<RentalResponseDTO> jejuResponses) {
        return CenterRentalListResponseDTO.builder()
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
