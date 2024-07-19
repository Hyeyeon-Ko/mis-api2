package kr.or.kmi.mis.api.std.model.response.bcd;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Builder
@Data
@AllArgsConstructor
public class StdBcdResponseDTO {

    List<StdBcdDetailResponseDTO> instInfo;
    List<StdBcdDetailResponseDTO> deptInfo;
    List<StdBcdDetailResponseDTO> teamInfo;
    List<StdBcdDetailResponseDTO> gradeInfo;

}
