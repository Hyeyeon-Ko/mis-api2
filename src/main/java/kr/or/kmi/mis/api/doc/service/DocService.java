package kr.or.kmi.mis.api.doc.service;

import kr.or.kmi.mis.api.doc.model.request.ReceiveDocRequestDTO;
import kr.or.kmi.mis.api.doc.model.request.SendDocRequestDTO;
import kr.or.kmi.mis.api.doc.model.request.DocUpdateRequestDTO;
import kr.or.kmi.mis.api.doc.model.response.DocDetailResponseDTO;
import kr.or.kmi.mis.api.doc.model.response.DocMasterResponseDTO;
import kr.or.kmi.mis.api.doc.model.response.DocMyResponseDTO;
import kr.or.kmi.mis.api.doc.model.response.DocPendingResponseDTO;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface DocService {

    void applyReceiveDoc(ReceiveDocRequestDTO receiveDocRequestDTO, MultipartFile file) throws IOException;
    void applySendDoc(SendDocRequestDTO sendDocRequestDTO, MultipartFile file) throws IOException;
    void updateDocApply(Long draftId, DocUpdateRequestDTO docUpdateRequestDTO, MultipartFile file, boolean isFileDeleted) throws IOException;
    void cancelDocApply(Long draftId);
    DocDetailResponseDTO getDoc(Long draftId);
    List<DocMyResponseDTO> getMyDocApply(String userId);
    List<DocPendingResponseDTO> getMyDocPendingList(String userId);
    List<DocMasterResponseDTO> getDocApplyByInstCd(String instCd, String userId);
    List<DocPendingResponseDTO> getDocPendingList(String instCd, String userId);
}
