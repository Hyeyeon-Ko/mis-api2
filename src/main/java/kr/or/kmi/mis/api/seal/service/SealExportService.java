package kr.or.kmi.mis.api.seal.service;

import kr.or.kmi.mis.api.seal.model.request.ExportRequestDTO;
import kr.or.kmi.mis.api.seal.model.request.ExportUpdateRequestDTO;

public interface SealExportService {

    /* 반출신청 */
    void applyExport(ExportRequestDTO exportRequestDTO);

    /* 반출수정 */
    void updateExport(Long draftId, ExportUpdateRequestDTO exportUpdateRequestDTO);

    /* 반출취소 */
    void cancelExport(Long draftId);
}
