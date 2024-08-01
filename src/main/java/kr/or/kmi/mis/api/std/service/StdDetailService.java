package kr.or.kmi.mis.api.std.service;

import kr.or.kmi.mis.api.std.model.request.StdDetailRequestDTO;
import kr.or.kmi.mis.api.std.model.request.StdDetailUpdateRequestDTO;
import kr.or.kmi.mis.api.std.model.response.StdDetailResponseDTO;

import java.util.List;

public interface StdDetailService {

    List<StdDetailResponseDTO> getInfo(String groupCd);
    void addInfo(StdDetailRequestDTO stdDetailRequestDTO);
    void updateInfo(StdDetailUpdateRequestDTO stdDetailRequestDTO);
    void deleteInfo(String groupCd, String detailCd);

}
