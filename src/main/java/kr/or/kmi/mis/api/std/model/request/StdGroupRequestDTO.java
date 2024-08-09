package kr.or.kmi.mis.api.std.model.request;

import com.fasterxml.jackson.databind.BeanProperty;
import kr.or.kmi.mis.api.std.model.entity.StdClass;
import kr.or.kmi.mis.api.std.model.entity.StdGroup;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
@AllArgsConstructor
public class StdGroupRequestDTO {

    String classCd;
    String groupCd;
    String groupNm;

    public StdGroup toEntity(StdClass stdClass) {
        return StdGroup.builder()
                .classCd(stdClass)
                .groupCd(groupCd)
                .groupNm(groupNm)
                .build();
    }
}
