package kr.or.kmi.mis.api.seal.service;

import kr.or.kmi.mis.api.seal.model.request.ExportRequestDTO;
import kr.or.kmi.mis.api.seal.model.request.ExportUpdateRequestDTO;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface SealExportService {

    /* 반출신청 */
    void applyExport(ExportRequestDTO exportRequestDTO, MultipartFile file) throws IOException;

    /* 반출수정 */
    void updateExport(Long draftId, ExportUpdateRequestDTO exportUpdateRequestDTO, MultipartFile file, boolean isFileDeleted) throws IOException;

    /* 반출취소 */
    void cancelExport(Long draftId);
}
