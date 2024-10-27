package kr.or.kmi.mis.api.std.service;

import kr.or.kmi.mis.api.std.model.request.StdDetailRequestDTO;
import kr.or.kmi.mis.api.std.model.request.StdDetailUpdateRequestDTO;
import kr.or.kmi.mis.api.std.model.response.StdDetailResponseDTO;
import kr.or.kmi.mis.api.std.model.response.StdResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface StdDetailService {

    List<StdDetailResponseDTO> getInfo(String groupCd);
    // 페이징 처리
    Page<StdDetailResponseDTO> getInfo2(String groupCd, Pageable page);
    List<StdDetailResponseDTO> getHeaderInfo(String groupCd);
    void addInfo(StdDetailRequestDTO stdDetailRequestDTO);
    void updateInfo(StdDetailUpdateRequestDTO stdDetailRequestDTO, String oriDetailCd);
    void deleteInfo(String groupCd, String detailCd);
    StdDetailResponseDTO getSelectedInfo(String groupCd, String detailCd);
    List<StdResponseDTO> getOrgChart(String instCd, String deptCode);
}
