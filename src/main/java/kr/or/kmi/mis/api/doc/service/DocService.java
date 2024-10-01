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
import java.sql.Timestamp;
import java.util.List;

public interface DocService {

    void applyReceiveDoc(ReceiveDocRequestDTO receiveDocRequestDTO, MultipartFile file) throws IOException;
    void applyReceiveDocByLeader(ReceiveDocRequestDTO receiveDocRequestDTO, MultipartFile file) throws IOException;
    void applySendDoc(SendDocRequestDTO sendDocRequestDTO, MultipartFile file) throws IOException;
    void applySendDocByLeader(SendDocRequestDTO sendDocRequestDTO, MultipartFile file) throws IOException;
    void updateDocApply(String draftId, DocUpdateRequestDTO docUpdateRequestDTO, MultipartFile file, boolean isFileDeleted) throws IOException;
    void cancelDocApply(String draftId);
    DocDetailResponseDTO getDoc(String draftId);
    List<DocMyResponseDTO> getMyDocApply(Timestamp startDate, Timestamp endDate, String userId);
    List<DocPendingResponseDTO> getMyDocPendingList(String userId);
    List<DocMasterResponseDTO> getDocApply(Timestamp startDate, Timestamp endDate, String searchType, String keyword, String instCd, String userId);
    List<DocPendingResponseDTO> getDocPendingList(Timestamp startDate, Timestamp endDate, String instCd, String userId);
}
