package kr.or.kmi.mis.api.main.model.response;

import kr.or.kmi.mis.api.bcd.model.response.BcdMasterResponseDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

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

    public static ApplyResponseDTO of(List<BcdMasterResponseDTO> bcdMasterResponses) {
        return ApplyResponseDTO.builder()
                .bcdMasterResponses(bcdMasterResponses)
                .build();
    }

}
