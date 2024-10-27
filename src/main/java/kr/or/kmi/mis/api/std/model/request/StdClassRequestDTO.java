package kr.or.kmi.mis.api.std.model.request;

import kr.or.kmi.mis.api.std.model.entity.StdClass;
import lombok.Getter;

@Getter
public class StdClassRequestDTO {

    String classCd;
    String classNm;
    String userId;

    public StdClass toEntity() {
        return StdClass.builder()
                .classCd(classCd)
                .classNm(classNm).build();
    }
}
