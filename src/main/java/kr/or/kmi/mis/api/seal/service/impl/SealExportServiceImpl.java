package kr.or.kmi.mis.api.seal.service.impl;

import kr.or.kmi.mis.api.seal.model.entity.SealExportDetail;
import kr.or.kmi.mis.api.seal.model.entity.SealMaster;
import kr.or.kmi.mis.api.seal.model.request.ExportRequestDTO;
import kr.or.kmi.mis.api.seal.model.request.ExportUpdateRequestDTO;
import kr.or.kmi.mis.api.seal.repository.SealExportDetailRepository;
import kr.or.kmi.mis.api.seal.repository.SealMasterRepository;
import kr.or.kmi.mis.api.seal.service.SealExportHistoryService;
import kr.or.kmi.mis.api.seal.service.SealExportService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;

@Service
@RequiredArgsConstructor
public class SealExportServiceImpl implements SealExportService {

    private final SealMasterRepository sealMasterRepository;
    private final SealExportDetailRepository sealExportDetailRepository;
    private final SealExportHistoryService sealExportHistoryService;

    @Override
    @Transactional
    public void applyExport(ExportRequestDTO exportRequestDTO) {

        SealMaster sealMaster = exportRequestDTO.toMasterEntity();
        sealMaster.setRgstrId(exportRequestDTO.getDrafterId());
        sealMaster.setRgstDt(new Timestamp(System.currentTimeMillis()));
        sealMaster = sealMasterRepository.save(sealMaster);

        Long draftId = sealMaster.getDraftId();
        SealExportDetail sealExportDetail = exportRequestDTO.toDetailEntity(draftId);
        sealExportDetail.setRgstrId(exportRequestDTO.getDrafterId());
        sealExportDetail.setRgstDt(new Timestamp(System.currentTimeMillis()));
        sealExportDetailRepository.save(sealExportDetail);
    }

    @Override
    @Transactional
    public void updateExport(Long draftId, ExportUpdateRequestDTO exportUpdateRequestDTO) {

        SealMaster sealMaster = sealMasterRepository.findById(draftId)
                .orElseThrow(() -> new IllegalArgumentException("Not Found"));

        // 반출신청 상세 조회
        SealExportDetail sealExportDetailInfo = sealExportDetailRepository.findById(draftId)
                .orElseThrow(() -> new IllegalArgumentException("Not Found"));

        // 반출신청 히스토리 저장
        sealExportHistoryService.createSealExportHistory(sealExportDetailInfo);

        // 반출신청 수정사항 저장
        sealExportDetailInfo.update(exportUpdateRequestDTO);
        sealMaster.setUpdtrId(sealMaster.getDrafterId());
        sealMaster.setUpdtDt(new Timestamp(System.currentTimeMillis()));
        sealExportDetailRepository.save(sealExportDetailInfo);

        sealExportDetailInfo.setUpdtrId(sealMaster.getDrafterId());
        sealExportDetailInfo.setUpdtDt(new Timestamp(System.currentTimeMillis()));
        sealExportDetailRepository.save(sealExportDetailInfo);
    }

    @Override
    @Transactional
    public void cancelExport(Long draftId) {

        SealMaster sealMaster = sealMasterRepository.findById(draftId)
                .orElseThrow(() -> new IllegalArgumentException("Not Found"));

        sealMaster.updateStatus("F");
        sealMasterRepository.save(sealMaster);
    }
}
