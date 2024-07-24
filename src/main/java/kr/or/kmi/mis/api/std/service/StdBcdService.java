package kr.or.kmi.mis.api.std.service;

import kr.or.kmi.mis.api.bcd.model.entity.BcdDetail;
import kr.or.kmi.mis.api.std.model.response.bcd.StdBcdResponseDTO;
import kr.or.kmi.mis.api.std.model.response.bcd.StdStatusResponseDTO;

import java.util.List;

public interface StdBcdService {

    StdBcdResponseDTO getAllBcdStd();
    List<String> getBcdStdNames(BcdDetail bcdDetail);
    String getInstNm(String instCd);
    String getDeptNm(String deptCd);
    List<String> getTeamNm(String teamCd);
    List<String> getGradeNm(String gradeCd);
    List<StdStatusResponseDTO> getApplyStatus();
}
