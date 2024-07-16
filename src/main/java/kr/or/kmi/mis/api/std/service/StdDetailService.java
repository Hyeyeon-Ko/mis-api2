package kr.or.kmi.mis.api.std.service;

import kr.or.kmi.mis.api.std.model.request.StdDetailRequestDTO;
import kr.or.kmi.mis.api.std.model.response.StdDetailResponseDTO;

import java.util.List;

public interface StdDetailService {

//    public List<StdDetailResponseDTO> getInfo(String clsCd, String etcCd);
    public void addInfo(StdDetailRequestDTO stdDetailRequestDTO);
//    public void updateInfo(StdDetailRequestDTO stdDetailRequestDTO);
//    public void deleteInfo(String clsCd, String etcCd, String etcDetlCd);

}
