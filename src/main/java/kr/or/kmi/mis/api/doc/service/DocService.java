package kr.or.kmi.mis.api.doc.service;

import kr.or.kmi.mis.api.doc.model.request.DocRequestDTO;
import kr.or.kmi.mis.api.doc.model.request.DocUpdateRequestDTO;
import kr.or.kmi.mis.api.doc.model.response.*;

import java.sql.Timestamp;
import java.util.List;


public interface DocService {

    void applyDoc(DocRequestDTO docRequestDTO);
    void updateDocApply(Long draftId, DocUpdateRequestDTO docUpdateRequestDTO);
    void cancelDocApply(Long draftId);
    DocDetailResponseDTO getDoc(Long draftId);
    List<DocMyResponseDTO> getMyDocApplyByDateRange(Timestamp startTime, Timestamp endTime);
    List<DocPendingResponseDTO> getMyDocPendingList();
    List<DocMasterResponseDTO> getDocApplyByDateRange(Timestamp timestamp, Timestamp timestamp1);
    List<DocPendingResponseDTO> getDocPendingList();
}
