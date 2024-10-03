package kr.or.kmi.mis.api.doc.service;

import kr.or.kmi.mis.api.apply.model.request.ApplyRequestDTO;
import kr.or.kmi.mis.api.doc.model.request.ReceiveDocRequestDTO;
import kr.or.kmi.mis.api.doc.model.request.SendDocRequestDTO;
import kr.or.kmi.mis.api.doc.model.request.DocUpdateRequestDTO;
import kr.or.kmi.mis.api.doc.model.response.DocDetailResponseDTO;
import kr.or.kmi.mis.api.doc.model.response.DocMasterResponseDTO;
import kr.or.kmi.mis.api.doc.model.response.DocMyResponseDTO;
import kr.or.kmi.mis.api.doc.model.response.DocPendingResponseDTO;
import kr.or.kmi.mis.cmm.model.request.PostSearchRequestDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

public interface DocService {

    void applyReceiveDoc(ReceiveDocRequestDTO receiveDocRequestDTO, MultipartFile file) throws IOException;
    void applyReceiveDocByLeader(ReceiveDocRequestDTO receiveDocRequestDTO, MultipartFile file) throws IOException;
    void applySendDoc(SendDocRequestDTO sendDocRequestDTO, MultipartFile file) throws IOException;
    void applySendDocByLeader(SendDocRequestDTO sendDocRequestDTO, MultipartFile file) throws IOException;
    void updateDocApply(String draftId, DocUpdateRequestDTO docUpdateRequestDTO, MultipartFile file, boolean isFileDeleted) throws IOException;
    void cancelDocApply(String draftId);
    DocDetailResponseDTO getDoc(String draftId);
    List<DocMyResponseDTO> getMyDocApply(LocalDateTime startDate, LocalDateTime endDate, String userId);
    List<DocPendingResponseDTO> getMyDocPendingList(String userId);
    List<DocMasterResponseDTO> getDocApply(LocalDateTime startDate, LocalDateTime endDate, String searchType, String keyword, String instCd, String userId);
    Page<DocMasterResponseDTO> getDocApply2(ApplyRequestDTO applyRequestDTO, PostSearchRequestDTO postSearchRequestDTO, Pageable pageable);
    List<DocPendingResponseDTO> getDocPendingList(LocalDateTime startDate, LocalDateTime endDate, String instCd, String userId);
}
