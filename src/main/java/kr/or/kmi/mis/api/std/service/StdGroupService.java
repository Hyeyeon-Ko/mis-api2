package kr.or.kmi.mis.api.std.service;

import kr.or.kmi.mis.api.std.model.entity.StdGroup;
import kr.or.kmi.mis.api.std.model.request.StdGroupRequestDTO;
import kr.or.kmi.mis.api.std.model.response.StdGroupResponseDTO;

import java.util.List;

public interface StdGroupService {

    List<StdGroupResponseDTO> getInfo(String classCd);
    void addInfo(StdGroupRequestDTO stdGroupRequestDTO);
    boolean findStdGroupAndCheckFirstApprover(String groupCd, String firstApproverId);
}
