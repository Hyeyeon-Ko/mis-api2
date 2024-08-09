package kr.or.kmi.mis.api.doc.service;

import kr.or.kmi.mis.api.doc.model.response.DocResponseDTO;

import java.time.LocalDate;
import java.util.List;

public interface DocListService {
    List<DocResponseDTO> getReceiveApplyList(LocalDate startDate, LocalDate endDate);
    List<DocResponseDTO> getSendApplyList(LocalDate startDate, LocalDate endDate);
}