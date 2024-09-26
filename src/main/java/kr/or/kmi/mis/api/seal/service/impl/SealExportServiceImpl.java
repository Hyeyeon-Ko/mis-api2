package kr.or.kmi.mis.api.seal.service.impl;

import kr.or.kmi.mis.api.seal.model.entity.SealExportDetail;
import kr.or.kmi.mis.api.seal.model.entity.SealMaster;
import kr.or.kmi.mis.api.seal.model.request.ExportRequestDTO;
import kr.or.kmi.mis.api.seal.model.request.ExportUpdateRequestDTO;
import kr.or.kmi.mis.api.seal.model.response.SealExportDetailResponseDTO;
import kr.or.kmi.mis.api.seal.repository.SealExportDetailRepository;
import kr.or.kmi.mis.api.seal.repository.SealMasterRepository;
import kr.or.kmi.mis.api.seal.service.SealExportHistoryService;
import kr.or.kmi.mis.api.seal.service.SealExportService;
import kr.or.kmi.mis.config.SftpClient;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.sql.Timestamp;

@Service
@RequiredArgsConstructor
public class SealExportServiceImpl implements SealExportService {

    private final SealMasterRepository sealMasterRepository;
    private final SealExportDetailRepository sealExportDetailRepository;
    private final SealExportHistoryService sealExportHistoryService;

    private final SftpClient sftpClient;

    @Value("${sftp.remote-directory.export}")
    private String exportRemoteDirectory;

    private String[] handleFileUpload(MultipartFile file, String existingFileName, String remoteDirectory) throws IOException {
        String fileName = null;
        String filePath = null;

        if (file != null && !file.isEmpty()) {
            fileName = file.getOriginalFilename();

            try {
                sftpClient.uploadFile(file, fileName, remoteDirectory);
                filePath = remoteDirectory + "/" + fileName;

                if (existingFileName != null) {
                    deleteFileFromSftp(existingFileName, remoteDirectory);
                }
            } catch (Exception e) {
                throw new IOException("SFTP 파일 업로드 실패: " + fileName, e);
            }
        }

        return new String[]{fileName, filePath};
    }

    private void deleteFileFromSftp(String fileName, String remoteDirectory) throws IOException {
        if (fileName != null) {
            try {
                sftpClient.deleteFile(fileName, remoteDirectory);
            } catch (Exception e) {
                throw new IOException("SFTP 파일 삭제 실패: " + fileName, e);
            }
        }
    }

    @Override
    @Transactional
    public void applyExport(ExportRequestDTO exportRequestDTO, MultipartFile file) throws IOException {
        SealMaster sealMaster = exportRequestDTO.toMasterEntity();
        sealMaster.setRgstrId(exportRequestDTO.getDrafterId());
        sealMaster.setRgstDt(new Timestamp(System.currentTimeMillis()));
        sealMaster = sealMasterRepository.save(sealMaster);

        String[] savedFileInfo = handleFileUpload(file, null, exportRemoteDirectory);

        SealExportDetail sealExportDetail = exportRequestDTO.toDetailEntity(sealMaster.getDraftId(), savedFileInfo[0], savedFileInfo[1]);
        sealExportDetail.setRgstrId(exportRequestDTO.getDrafterId());
        sealExportDetail.setRgstDt(new Timestamp(System.currentTimeMillis()));
        sealExportDetailRepository.save(sealExportDetail);
    }

    @Override
    @Transactional
    public void updateExport(Long draftId, ExportUpdateRequestDTO exportUpdateRequestDTO, MultipartFile file, boolean isFileDeleted) throws IOException {
        SealMaster sealMaster = sealMasterRepository.findById(draftId)
                .orElseThrow(() -> new IllegalArgumentException("Not Found"));

        SealExportDetail sealExportDetailInfo = sealExportDetailRepository.findById(draftId)
                .orElseThrow(() -> new IllegalArgumentException("Not Found"));

        sealExportHistoryService.createSealExportHistory(sealExportDetailInfo);

        String[] savedFileInfo;
        if (file != null) {
            savedFileInfo = handleFileUpload(file, sealExportDetailInfo.getFileName(), exportRemoteDirectory);
        } else if (isFileDeleted) {
            savedFileInfo = new String[]{null, null};
            deleteFileFromSftp(sealExportDetailInfo.getFileName(), exportRemoteDirectory);
        } else {
            savedFileInfo = new String[]{sealExportDetailInfo.getFileName(), sealExportDetailInfo.getFilePath()};
        }

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
    @Transactional(readOnly = true)
    public SealExportDetailResponseDTO getSealExportDetail(Long draftId) {
        SealExportDetail sealExportDetail = sealExportDetailRepository.findById(draftId)
                .orElseThrow(() -> new IllegalArgumentException("Not Found"));

        return SealExportDetailResponseDTO.of(sealExportDetail);
    }
}
