package kr.or.kmi.mis.api.doc.service;

import kr.or.kmi.mis.api.doc.model.response.DocResponseDTO;

import java.util.List;

public interface DocSRService {
    List<DocResponseDTO> getReceiveApplyList();

    List<DocResponseDTO> getSendApplyList();
}
