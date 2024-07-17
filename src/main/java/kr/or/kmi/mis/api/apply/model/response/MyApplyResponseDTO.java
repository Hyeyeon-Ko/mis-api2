package kr.or.kmi.mis.api.apply.model.response;

import kr.or.kmi.mis.api.bcd.model.response.BcdMyResponseDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Builder
@Data
@AllArgsConstructor
public class MyApplyResponseDTO {

    private List<BcdMyResponseDTO> myApplyResponses;

    public static MyApplyResponseDTO of(List<BcdMyResponseDTO> bcdMyResponses) {
        return MyApplyResponseDTO.builder()
                .myApplyResponses(bcdMyResponses)
                .build();
    }
}
