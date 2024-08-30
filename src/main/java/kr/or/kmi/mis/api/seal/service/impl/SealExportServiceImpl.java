package kr.or.kmi.mis.api.seal.service.impl;

import kr.or.kmi.mis.api.seal.model.entity.SealExportDetail;
import kr.or.kmi.mis.api.seal.model.entity.SealImprintDetail;
import kr.or.kmi.mis.api.seal.model.entity.SealMaster;
import kr.or.kmi.mis.api.seal.model.request.ExportRequestDTO;
import kr.or.kmi.mis.api.seal.model.request.ExportUpdateRequestDTO;
import kr.or.kmi.mis.api.seal.model.request.SealUpdateRequestDTO;
import kr.or.kmi.mis.api.seal.model.response.SealExportDetailResponseDTO;
import kr.or.kmi.mis.api.seal.repository.SealExportDetailRepository;
import kr.or.kmi.mis.api.seal.repository.SealMasterRepository;
import kr.or.kmi.mis.api.seal.service.SealExportHistoryService;
import kr.or.kmi.mis.api.seal.service.SealExportService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Timestamp;

@Service
@RequiredArgsConstructor
public class SealExportServiceImpl implements SealExportService {

    private final SealMasterRepository sealMasterRepository;
    private final SealExportDetailRepository sealExportDetailRepository;
    private final SealExportHistoryService sealExportHistoryService;

    @Value("${file.upload-dir}")
    private String uploadDir;


    @Override
    @Transactional
    public void applyExport(ExportRequestDTO exportRequestDTO, MultipartFile file) throws IOException{

        SealMaster sealMaster = exportRequestDTO.toMasterEntity();
        sealMaster.setRgstrId(exportRequestDTO.getDrafterId());
        sealMaster.setRgstDt(new Timestamp(System.currentTimeMillis()));
        sealMaster = sealMasterRepository.save(sealMaster);

        Long draftId = sealMaster.getDraftId();
        String[] savedFileInfo = saveFile(file);

        SealExportDetail sealExportDetail = exportRequestDTO.toDetailEntity(draftId, savedFileInfo[0], savedFileInfo[1]);
        sealExportDetail.setRgstrId(exportRequestDTO.getDrafterId());
        sealExportDetail.setRgstDt(new Timestamp(System.currentTimeMillis()));
        sealExportDetailRepository.save(sealExportDetail);
    }

    @Override
    @Transactional
    public void updateExport(Long draftId, ExportUpdateRequestDTO exportUpdateRequestDTO, MultipartFile file, boolean isFileDeleted) throws IOException {

        SealMaster sealMaster = sealMasterRepository.findById(draftId)
                .orElseThrow(() -> new IllegalArgumentException("Not Found"));

        // 반출신청 상세 조회
        SealExportDetail sealExportDetailInfo = sealExportDetailRepository.findById(draftId)
                .orElseThrow(() -> new IllegalArgumentException("Not Found"));

        // 반출신청 히스토리 저장
        sealExportHistoryService.createSealExportHistory(sealExportDetailInfo);

        String[] savedFileInfo;
        if (file != null) {
            savedFileInfo = saveFile(file, sealExportDetailInfo.getFilePath());
        } else if (isFileDeleted) {
            savedFileInfo = new String[]{null, null};
            if (sealExportDetailInfo.getFilePath() != null) {
                Path filePath = Paths.get(sealExportDetailInfo.getFilePath());
                Files.deleteIfExists(filePath);
            }
        } else {
            savedFileInfo = new String[]{sealExportDetailInfo.getFileName(), sealExportDetailInfo.getFilePath()};
        }

        // 반출신청 수정사항 저장
        updateSealExportDetail(exportUpdateRequestDTO, draftId, savedFileInfo);
        sealMaster.setUpdtrId(sealExportDetailInfo.getRgstrId());
        sealMaster.setUpdtDt(new Timestamp(System.currentTimeMillis()));
        sealMasterRepository.save(sealMaster);
    }

    private void updateSealExportDetail(Object exportRequestOrUpdateDTO, Long draftId, String[] savedFileInfo) {
        SealExportDetail existingSealImprintDetail = sealExportDetailRepository.findById(draftId)
                .orElseThrow(() -> new IllegalArgumentException("Not Found"));

        if (exportRequestOrUpdateDTO instanceof ExportRequestDTO exportRequestDTO) {
            existingSealImprintDetail.update(exportRequestDTO, savedFileInfo[0], savedFileInfo[1]);
        } else if (exportRequestOrUpdateDTO instanceof ExportUpdateRequestDTO exportUpdateRequestDTO) {
            existingSealImprintDetail.updateFile(exportUpdateRequestDTO, savedFileInfo[0], savedFileInfo[1]);
        }

        sealExportDetailRepository.save(existingSealImprintDetail);
    }

    @Override
    @Transactional
    public void cancelExport(Long draftId) {

        SealMaster sealMaster = sealMasterRepository.findById(draftId)
                .orElseThrow(() -> new IllegalArgumentException("Not Found"));

        sealMaster.updateStatus("F");
        sealMasterRepository.save(sealMaster);
    }

    @Override
    public SealExportDetailResponseDTO getSealExportDetail(Long draftId) {
        SealExportDetail sealExportDetail = sealExportDetailRepository.findById(draftId)
                .orElseThrow(() -> new IllegalArgumentException("Not Found"));

        return SealExportDetailResponseDTO.of(sealExportDetail);
    }

    private String[] saveFile(MultipartFile file) throws IOException {
        return saveFile(file, null);
    }

    private String[] saveFile(MultipartFile file, String existingFilePath) throws IOException {
        String fileName = null;
        String filePath = null;

        if (file != null && !file.isEmpty()) {
            String originalFileName = file.getOriginalFilename();
            String baseFileName = originalFileName.substring(0, originalFileName.lastIndexOf("."));
            String fileExtension = originalFileName.substring(originalFileName.lastIndexOf("."));
            fileName = baseFileName.replaceAll("\\s+", "_") + fileExtension;

            Path fileStoragePath = Paths.get(uploadDir).toAbsolutePath().normalize();
            if (Files.notExists(fileStoragePath)) {
                Files.createDirectories(fileStoragePath);
            }

            Path targetLocation = fileStoragePath.resolve(fileName);

            int count = 1;
            while (Files.exists(targetLocation)) {
                String newFileName = baseFileName.replaceAll("\\s+", "_") + " (" + count + ")" + fileExtension;
                targetLocation = fileStoragePath.resolve(newFileName);
                count++;
            }

            fileName = targetLocation.getFileName().toString();
            Files.copy(file.getInputStream(), targetLocation);
            filePath = targetLocation.toString();

            if (existingFilePath != null) {
                Path oldFilePath = Paths.get(existingFilePath);
                Files.deleteIfExists(oldFilePath);
            }
        }

        return new String[]{fileName, filePath};
    }
}
