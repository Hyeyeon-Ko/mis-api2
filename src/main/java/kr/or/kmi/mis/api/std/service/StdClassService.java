package kr.or.kmi.mis.api.std.service;

import kr.or.kmi.mis.api.std.model.request.StdClassRequestDTO;
import kr.or.kmi.mis.api.std.model.response.StdClassResponseDTO;

import java.util.List;

public interface StdClassService {

    /* 대분류코드 정보 호출 */
    List<StdClassResponseDTO> getInfo();

    /* 대분류코드 추가 */
    void addClassInfo(StdClassRequestDTO stdClassRequestDTO);
}
