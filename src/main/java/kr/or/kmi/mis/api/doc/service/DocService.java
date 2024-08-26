package kr.or.kmi.mis.api.doc.service;

import kr.or.kmi.mis.api.doc.model.request.DocRequestDTO;
import kr.or.kmi.mis.api.doc.model.request.DocUpdateRequestDTO;
import kr.or.kmi.mis.api.doc.model.response.DocDetailResponseDTO;
import kr.or.kmi.mis.api.doc.model.response.DocMasterResponseDTO;
import kr.or.kmi.mis.api.doc.model.response.DocMyResponseDTO;
import kr.or.kmi.mis.api.doc.model.response.DocPendingResponseDTO;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.List;

public interface DocService {

    void applyDoc(DocRequestDTO docRequestDTO, MultipartFile file) throws IOException;
    void updateDocApply(Long draftId, DocUpdateRequestDTO docUpdateRequestDTO, MultipartFile file, boolean isFileDeleted) throws IOException;
    void cancelDocApply(Long draftId);
    DocDetailResponseDTO getDoc(Long draftId);
    List<DocMyResponseDTO> getMyDocApplyByDateRange(Timestamp startTime, Timestamp endTime);
    List<DocPendingResponseDTO> getMyDocPendingList();
    List<DocMasterResponseDTO> getDocApplyByDateRangeAndInstCd(Timestamp timestamp, Timestamp timestamp1, String instCd);
    List<DocPendingResponseDTO> getDocPendingList(String instCd);
}
