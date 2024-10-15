package kr.or.kmi.mis.api.seal.service.impl;

import kr.or.kmi.mis.api.file.model.entity.FileDetail;
import kr.or.kmi.mis.api.file.model.entity.FileHistory;
import kr.or.kmi.mis.api.file.model.request.FileUploadRequestDTO;
import kr.or.kmi.mis.api.file.repository.FileDetailRepository;
import kr.or.kmi.mis.api.file.repository.FileHistoryRepository;
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
import kr.or.kmi.mis.api.std.model.entity.StdDetail;
import kr.or.kmi.mis.api.std.model.entity.StdGroup;
import kr.or.kmi.mis.api.std.repository.StdDetailRepository;
import kr.or.kmi.mis.api.std.repository.StdGroupRepository;
import kr.or.kmi.mis.config.SftpClient;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SealExportServiceImpl implements SealExportService {

    private final FileService fileService;
    private final SealMasterRepository sealMasterRepository;
    private final SealExportDetailRepository sealExportDetailRepository;
    private final SealExportHistoryService sealExportHistoryService;

    private final SftpClient sftpClient;
    private final FileDetailRepository fileDetailRepository;
    private final FileHistoryRepository fileHistoryRepository;
    private final StdGroupRepository stdGroupRepository;
    private final StdDetailRepository stdDetailRepository;

    @Value("${sftp.remote-directory.export}")
    private String exportRemoteDirectory;

    private String[] handleFileUpload(String drafter, MultipartFile file, String existingFileName, String remoteDirectory) throws IOException {
        String fileName = null;
        String filePath = null;

        if (file != null && !file.isEmpty()) {
            fileName = LocalDateTime.now().toLocalDate() + "_" + drafter + "_" + file.getOriginalFilename();

            try {
                String newFileName = sftpClient.uploadFile(file, fileName, remoteDirectory);
                filePath = remoteDirectory + "/" + newFileName;

                if (existingFileName != null) {
                    deleteFileFromSftp(existingFileName, remoteDirectory);
                }

                return new String[]{newFileName, filePath};

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
        String draftId = generateDraftId();

        // 1. SealMaster 저장
        SealMaster sealMaster = exportRequestDTO.toMasterEntity(draftId);
        sealMaster.setRgstrId(exportRequestDTO.getDrafterId());
        sealMaster.setRgstDt(LocalDateTime.now());
        sealMaster = sealMasterRepository.save(sealMaster);

        // 2. SealDetail 저장
        SealExportDetail sealExportDetail = exportRequestDTO.toDetailEntity(sealMaster.getDraftId());
        sealExportDetail.setRgstrId(exportRequestDTO.getDrafterId());
        sealExportDetail.setRgstDt(LocalDateTime.now());
        sealExportDetailRepository.save(sealExportDetail);

        // 3. File 업로드
        String[] savedFileInfo = handleFileUpload(sealMaster.getDrafter(), file, null, exportRemoteDirectory);

        FileUploadRequestDTO fileUploadRequestDTO = FileUploadRequestDTO.builder()
                .draftId(draftId)
                .fileName(savedFileInfo[0])
                .filePath(savedFileInfo[1])
                .build();

        fileService.uploadFile(fileUploadRequestDTO);
    }

    private String generateDraftId() {
        Optional<SealMaster> lastSealMasterOpt = sealMasterRepository.findTopByOrderByDraftIdDesc();

        StdGroup stdGroup = stdGroupRepository.findByGroupCd("A007")
                .orElseThrow(() -> new IllegalArgumentException("Not Found"));
        StdDetail stdDetail = stdDetailRepository.findByGroupCdAndDetailCd(stdGroup, "D")
                .orElseThrow(() -> new IllegalArgumentException("Not Found"));

        if (lastSealMasterOpt.isPresent()) {
            String lastDraftId = lastSealMasterOpt.get().getDraftId();
            int lastIdNum = Integer.parseInt(lastDraftId.substring(2));
            return stdDetail.getEtcItem1() + String.format("%010d", lastIdNum + 1);
        } else {
            return stdDetail.getEtcItem1() + "0000000001";
        }
    }

    @Override
    @Transactional
    public void updateExport(String draftId, ExportUpdateRequestDTO exportUpdateRequestDTO, MultipartFile file, boolean isFileDeleted) throws IOException {

        // 1. SealExportMaster 업데이트
        SealMaster sealMaster = sealMasterRepository.findById(draftId)
                .orElseThrow(() -> new IllegalArgumentException("Not Found"));

        sealMaster.setUpdtrId(sealMaster.getDrafterId());
        sealMaster.setUpdtDt(LocalDateTime.now());
        sealMasterRepository.save(sealMaster);

        // 2. SealExportDetail 업데이트
        SealExportDetail sealExportDetailInfo = sealExportDetailRepository.findById(draftId)
                .orElseThrow(() -> new IllegalArgumentException("Not Found"));

        sealExportHistoryService.createSealExportHistory(sealExportDetailInfo);

        sealExportDetailInfo.setUpdtDt(LocalDateTime.now());
        sealExportDetailInfo.setUpdtrId(sealMaster.getDrafterId());
        updateSealExportDetail(exportUpdateRequestDTO, draftId);

        // 3. File 업데이트
        FileDetail fileDetail = fileDetailRepository.findByDraftId(draftId).orElse(null);
        FileHistory fileHistory = null;

        if (fileDetail != null) {
            fileHistory = fileHistoryRepository.findTopByAttachIdOrderBySeqIdDesc(fileDetail.getAttachId())
                    .orElseThrow(() -> new IllegalArgumentException("Not Found"));
        }

        if (file != null && !file.isEmpty()) {
            if (fileHistory != null && fileHistory.getFilePath() != null) {
                String filename = LocalDateTime.now().toLocalDate() + "_" + sealMaster.getDrafter() + "_" + file.getOriginalFilename();
                try {
                    sftpClient.deleteFile(fileHistory.getFileName(), exportRemoteDirectory);
                    String newFileName = sftpClient.uploadFile(file, filename, exportRemoteDirectory);
                    String newFilePath = exportRemoteDirectory + "/" + newFileName;
                    fileService.updateFile(new FileUploadRequestDTO(sealMaster.getDraftId(), sealMaster.getDrafterId(), newFileName, newFilePath));
                } catch (Exception e) {
                    throw new IOException("SFTP 기존 파일 삭제 실패", e);
                }
            } else {
                String filename = LocalDateTime.now().toLocalDate() + "_" + sealMaster.getDrafter() + "_" + file.getOriginalFilename();
                try {
                    String newFileName = sftpClient.uploadFile(file, filename, exportRemoteDirectory);
                    String newFilePath = exportRemoteDirectory + "/" + newFileName;
                    fileService.uploadFile(new FileUploadRequestDTO(sealMaster.getDraftId(), sealMaster.getDrafterId(), newFileName, newFilePath));
                } catch (Exception e) {
                    throw new IOException("SFTP 파일 업로드 실패", e);
                }
            }
        }
        else if (isFileDeleted && fileHistory != null && fileHistory.getFilePath() != null) {
            try {
                sftpClient.deleteFile(fileHistory.getFilePath(), exportRemoteDirectory);
                fileDetail.updateUseAt("N");
            } catch (Exception e) {
                throw new IOException("SFTP 파일 삭제 실패", e);
            }
        }
    }

    private void updateSealExportDetail(Object exportRequestOrUpdateDTO, String draftId) {
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
    public void cancelExport(String draftId) {

        // 1. SealMaster 삭제 (F)
        SealMaster sealMaster = sealMasterRepository.findById(draftId)
                .orElseThrow(() -> new IllegalArgumentException("Not Found"));
        sealMaster.updateStatus("F");
        sealMasterRepository.save(sealMaster);

        // 2. FileDetail History 업데이트
        FileDetail fileDetail = fileDetailRepository.findByDraftId(draftId)
                .orElseThrow(() -> new IllegalArgumentException("Not Found"));
        fileDetail.updateUseAt("N");
    }

    @Override
    @Transactional(readOnly = true)
    public SealExportDetailResponseDTO getSealExportDetail(String draftId) {
        SealExportDetail sealExportDetail = sealExportDetailRepository.findById(draftId)
                .orElseThrow(() -> new IllegalArgumentException("Not Found"));

        FileDetail fileDetail = fileDetailRepository.findByDraftId(sealExportDetail.getDraftId())
                .orElse(null);
        FileHistory fileHistory = null;
        if (fileDetail != null) {
            fileHistory = fileHistoryRepository.findTopByAttachIdOrderBySeqIdDesc(fileDetail.getAttachId())
                    .orElseThrow(() -> new IllegalArgumentException("Not Found"));
        }

        return SealExportDetailResponseDTO.of(sealExportDetail, fileHistory);
    }
}
