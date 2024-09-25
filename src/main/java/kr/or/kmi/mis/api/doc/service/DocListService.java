package kr.or.kmi.mis.api.doc.service;

import kr.or.kmi.mis.api.doc.model.response.DocResponseDTO;
import kr.or.kmi.mis.api.docstorage.domain.response.DeptResponseDTO;

import java.time.LocalDate;
import java.util.List;

public interface DocListService {
    List<DocResponseDTO> getReceiveApplyList(LocalDate startDate, LocalDate endDate, String searchType, String keyword, String instCd);
    List<DocResponseDTO> getDeptReceiveApplyList(String deptCd);
    List<DocResponseDTO> getSendApplyList(LocalDate startDate, LocalDate endDate, String searchType, String keyword, String instCd);
    List<DeptResponseDTO> getDeptList(String instCd);
}