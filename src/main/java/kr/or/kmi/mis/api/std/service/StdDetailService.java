package kr.or.kmi.mis.api.std.service;

import kr.or.kmi.mis.api.std.model.request.StdDetailRequestDTO;
import kr.or.kmi.mis.api.std.model.request.StdDetailUpdateRequestDTO;
import kr.or.kmi.mis.api.std.model.response.StdDetailResponseDTO;

import java.util.List;

public interface StdDetailService {

    public List<StdDetailResponseDTO> getInfo(String groupCd);
    public void addInfo(StdDetailRequestDTO stdDetailRequestDTO);
    public void updateInfo(StdDetailUpdateRequestDTO stdDetailRequestDTO);
    public void deleteInfo(String detailCd);

}
