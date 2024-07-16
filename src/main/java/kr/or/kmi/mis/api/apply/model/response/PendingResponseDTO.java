package kr.or.kmi.mis.api.apply.model.response;

import kr.or.kmi.mis.api.bcd.model.response.BcdPendingResponseDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Builder
@Data
@AllArgsConstructor
public class PendingResponseDTO {

    /**
     * 전체 승인대기 신청 목록 반환 DTO
     * 명함신청 이외에 다른 신청 (ex. 문서 수발신) 이 있을 경우
     * 해당 DTO 에 한번에 담아서 반환
     * */

    private List<BcdPendingResponseDTO> bcdPendingResponses;

    public static PendingResponseDTO of(List<BcdPendingResponseDTO> bcdPendingResponses) {
        return PendingResponseDTO.builder()
                .bcdPendingResponses(bcdPendingResponses)
                .build();
    }

}
