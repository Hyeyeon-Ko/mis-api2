package kr.or.kmi.mis.api.std.model.response;

import kr.or.kmi.mis.api.std.model.entity.StdDetail;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Builder
@Data
@AllArgsConstructor
public class StdBcdResponseDTO {

    List<StdDetail> centerInfo;
    List<StdDetail> deptInfo;
    List<StdDetail> teamInfo;
    List<StdDetail> gradeInfo;

}
