package kr.or.kmi.mis.api.seal.service;

import kr.or.kmi.mis.api.seal.model.entity.SealExportDetail;

public interface SealExportHistoryService {

    void createSealExportHistory(SealExportDetail sealExportDetail);

}
