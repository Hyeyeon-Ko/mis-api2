package kr.or.kmi.mis.api.seal.service.impl;

import kr.or.kmi.mis.api.seal.model.request.ExportRequestDTO;
import kr.or.kmi.mis.api.seal.model.request.ExportUpdateRequestDTO;
import kr.or.kmi.mis.api.seal.service.SealExportService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SealExportServiceImpl implements SealExportService {

    @Override
    public void applyExport(ExportRequestDTO exportRequestDTO) {

    }

    @Override
    public void updateExport(Long draftId, ExportUpdateRequestDTO exportUpdateRequestDTO) {

    }

    @Override
    public void cancelExport(Long draftId) {

    }
}
