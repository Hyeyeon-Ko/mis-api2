package kr.or.kmi.mis.api.doc.service;

import kr.or.kmi.mis.api.doc.model.response.docResponseDTO;

import java.util.List;

public interface DocSRService {
    List<docResponseDTO> getReceiveApplyList();

    List<docResponseDTO> getSendApplyList();
}
