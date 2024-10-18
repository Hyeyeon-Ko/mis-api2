package kr.or.kmi.mis.api.toner.model.request;

import kr.or.kmi.mis.api.toner.model.response.TonerExcelResponseDTO;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class TonerExcelRequestDTO {

    private List<TonerExcelResponseDTO> details;
    private String instCd;
    private String userId;
}
