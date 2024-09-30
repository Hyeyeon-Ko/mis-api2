package kr.or.kmi.mis.api.seal.service.impl;

import kr.or.kmi.mis.api.file.model.entity.FileDetail;
import kr.or.kmi.mis.api.file.model.request.FileUploadRequestDTO;
import kr.or.kmi.mis.api.file.repository.FileDetailRepository;
import kr.or.kmi.mis.api.file.service.FileHistorySevice;
import kr.or.kmi.mis.api.file.service.FileService;
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

    private final FileService fileService;
    private final FileHistorySevice fileHistorySevice;
    private final SealMasterRepository sealMasterRepository;
    private final SealExportDetailRepository sealExportDetailRepository;
    private final SealExportHistoryService sealExportHistoryService;

    private final SftpClient sftpClient;
    private final FileDetailRepository fileDetailRepository;

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

        // 1. SealMaster 저장
        SealMaster sealMaster = exportRequestDTO.toMasterEntity();
        sealMaster.setRgstrId(exportRequestDTO.getDrafterId());
        sealMaster.setRgstDt(new Timestamp(System.currentTimeMillis()));
        sealMaster = sealMasterRepository.save(sealMaster);

        // 2. SealDetail 저장
        SealExportDetail sealExportDetail = exportRequestDTO.toDetailEntity(sealMaster.getDraftId());
        sealExportDetail.setRgstrId(exportRequestDTO.getDrafterId());
        sealExportDetail.setRgstDt(new Timestamp(System.currentTimeMillis()));
        sealExportDetailRepository.save(sealExportDetail);

        // 3. File 업로드
        String[] savedFileInfo = handleFileUpload(file, null, exportRemoteDirectory);

        FileUploadRequestDTO fileUploadRequestDTO = FileUploadRequestDTO.builder()
                .draftId(sealExportDetail.getDraftId())
                .drafter(sealMaster.getDrafter())
                .docType("A")
                .fileName(savedFileInfo[0])
                .filePath(savedFileInfo[1])
                .build();

        fileService.uploadFile(fileUploadRequestDTO);
    }

    @Override
    @Transactional
    public void updateExport(Long draftId, ExportUpdateRequestDTO exportUpdateRequestDTO, MultipartFile file, boolean isFileDeleted) throws IOException {

        // 1. SealExportMaster 업데이트
        SealMaster sealMaster = sealMasterRepository.findById(draftId)
                .orElseThrow(() -> new IllegalArgumentException("Not Found"));

        sealMaster.setUpdtrId(sealMaster.getDrafterId());
        sealMaster.setUpdtDt(new Timestamp(System.currentTimeMillis()));
        sealMasterRepository.save(sealMaster);

        // 2. SealExportDetail 업데이트
        SealExportDetail sealExportDetailInfo = sealExportDetailRepository.findById(draftId)
                .orElseThrow(() -> new IllegalArgumentException("Not Found"));

        sealExportHistoryService.createSealExportHistory(sealExportDetailInfo);

        sealExportDetailInfo.setUpdtDt(new Timestamp(System.currentTimeMillis()));
        sealExportDetailInfo.setUpdtrId(sealMaster.getDrafterId());
        updateSealExportDetail(exportUpdateRequestDTO, draftId);

        // 3. FileDetail 업데이트
        FileDetail fileDetail = fileDetailRepository.findByDraftIdAndDocType(draftId, "A")
                .orElse(null);

        if (fileDetail != null) fileHistorySevice.createFileHistory(fileDetail, "A");

        assert fileDetail != null;
        String[] savedFileInfo = {fileDetail.getFileName(), fileDetail.getFilePath()};

        if (file != null && !file.isEmpty()) {
            if (savedFileInfo[1] != null) {
                try {
                    sftpClient.deleteFile(savedFileInfo[1], exportRemoteDirectory);
                } catch (Exception e) {
                    throw new IOException("SFTP 기존 파일 삭제 실패", e);
                }
            }

            String newFileName = file.getOriginalFilename();
            try {
                sftpClient.uploadFile(file, newFileName, exportRemoteDirectory);
                savedFileInfo[0] = newFileName;
                savedFileInfo[1] = exportRemoteDirectory + "/" + newFileName;
            } catch (Exception e) {
                throw new IOException("SFTP 파일 업로드 실패", e);
            }
        }
        else if (isFileDeleted && savedFileInfo[1] != null) {
            try {
                sftpClient.deleteFile(savedFileInfo[1], exportRemoteDirectory);
                savedFileInfo[0] = null;
                savedFileInfo[1] = null;
            } catch (Exception e) {
                throw new IOException("SFTP 파일 삭제 실패", e);
            }
        }

        fileDetail.updateFileInfo(savedFileInfo[0], savedFileInfo[1]);
        fileDetail.setUpdtDt(new Timestamp(System.currentTimeMillis()));
        fileDetail.setUpdtrId(sealMaster.getDrafter());
        fileDetailRepository.save(fileDetail);
    }

    private void updateSealExportDetail(Object exportRequestOrUpdateDTO, Long draftId) {
        SealExportDetail existingSealImprintDetail = sealExportDetailRepository.findById(draftId)
                .orElseThrow(() -> new IllegalArgumentException("Not Found"));

        if (exportRequestOrUpdateDTO instanceof ExportRequestDTO exportRequestDTO) {
            existingSealImprintDetail.update(exportRequestDTO);
        } else if (exportRequestOrUpdateDTO instanceof ExportUpdateRequestDTO exportUpdateRequestDTO) {
            existingSealImprintDetail.updateFile(exportUpdateRequestDTO);
        }

        sealExportDetailRepository.save(existingSealImprintDetail);
    }

    @Override
    @Transactional
    public void cancelExport(Long draftId) {

        // 1. SealMaster 삭제 (F)
        SealMaster sealMaster = sealMasterRepository.findById(draftId)
                .orElseThrow(() -> new IllegalArgumentException("Not Found"));
        sealMaster.updateStatus("F");
        sealMasterRepository.save(sealMaster);

        // 2. FileDetail History 업데이트
        FileDetail fileDetail = fileDetailRepository.findByDraftIdAndDocType(draftId, "A")
                .orElseThrow(() -> new IllegalArgumentException("Not Found"));
        fileHistorySevice.createFileHistory(fileDetail, "B");
        fileDetailRepository.delete(fileDetail);
    }

    @Override
    @Transactional(readOnly = true)
    public SealExportDetailResponseDTO getSealExportDetail(Long draftId) {
        SealExportDetail sealExportDetail = sealExportDetailRepository.findById(draftId)
                .orElseThrow(() -> new IllegalArgumentException("Not Found"));

        FileDetail fileDetail = fileDetailRepository.findByDraftIdAndDocType(sealExportDetail.getDraftId(), "A")
                .orElseThrow(() -> new IllegalArgumentException("Not Found"));

        return SealExportDetailResponseDTO.of(sealExportDetail, fileDetail);
    }
}
