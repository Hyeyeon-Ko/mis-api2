package kr.or.kmi.mis.api.std.service;

import kr.or.kmi.mis.api.std.model.request.StdGroupRequestDTO;
import kr.or.kmi.mis.api.std.model.response.StdGroupResponseDTO;

import java.util.List;

public interface StdGroupService {

    public List<StdGroupResponseDTO> getInfo(String classCd);
    public void addInfo(StdGroupRequestDTO stdGroupRequestDTO);

}
