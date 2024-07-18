package kr.or.kmi.mis.api.std.service;

import kr.or.kmi.mis.api.std.model.response.StdBcdResponseDTO;

public interface StdBcdService {

    StdBcdResponseDTO getAllBcdStd();
//    List<String> getBcdStdNames(String detailCd);

    String getInstNm(String instCd);
    String getDeptNm(String deptCd);
    String getTeamNm(String teamCd);
    String getGradeNm(String gradeCd);
}
