package kr.or.kmi.mis.api.std.service;

import kr.or.kmi.mis.api.std.model.request.StdDetailRequestDTO;
import kr.or.kmi.mis.api.std.model.request.StdDetailUpdateRequestDTO;
import kr.or.kmi.mis.api.std.model.response.StdDetailResponseDTO;
import kr.or.kmi.mis.api.std.model.response.StdResponseDTO;

import java.util.List;

public interface StdDetailService {

    List<StdDetailResponseDTO> getInfo(String groupCd);
    List<StdDetailResponseDTO> getHeaderInfo(String groupCd);
    void addInfo(StdDetailRequestDTO stdDetailRequestDTO);
    void updateInfo(StdDetailUpdateRequestDTO stdDetailRequestDTO, String oriDetailCd);
    void deleteInfo(String groupCd, String detailCd);
    StdDetailResponseDTO getSelectedInfo(String groupCd, String detailCd);
    List<StdResponseDTO> getOrgChart(String instCd);
}
