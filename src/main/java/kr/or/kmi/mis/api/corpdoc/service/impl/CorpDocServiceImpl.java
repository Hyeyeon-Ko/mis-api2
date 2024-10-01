package kr.or.kmi.mis.api.corpdoc.service.impl;

import kr.or.kmi.mis.api.bcd.model.entity.BcdMaster;
import kr.or.kmi.mis.api.corpdoc.model.entity.CorpDocDetail;
import kr.or.kmi.mis.api.corpdoc.model.entity.CorpDocMaster;
import kr.or.kmi.mis.api.corpdoc.model.request.CorpDocRequestDTO;
import kr.or.kmi.mis.api.corpdoc.model.request.CorpDocUpdateRequestDTO;
import kr.or.kmi.mis.api.corpdoc.model.response.CorpDocDetailResponseDTO;
import kr.or.kmi.mis.api.corpdoc.model.response.CorpDocMasterResponseDTO;
import kr.or.kmi.mis.api.corpdoc.model.response.CorpDocMyResponseDTO;
import kr.or.kmi.mis.api.corpdoc.model.response.CorpDocPendingResponseDTO;
import kr.or.kmi.mis.api.corpdoc.repository.CorpDocDetailRepository;
import kr.or.kmi.mis.api.corpdoc.repository.CorpDocMasterRepository;
import kr.or.kmi.mis.api.corpdoc.service.CorpDocHistoryService;
import kr.or.kmi.mis.api.corpdoc.service.CorpDocService;
import kr.or.kmi.mis.api.file.model.entity.FileDetail;
import kr.or.kmi.mis.api.file.model.entity.FileHistory;
import kr.or.kmi.mis.api.file.model.request.FileUploadRequestDTO;
import kr.or.kmi.mis.api.file.repository.FileDetailRepository;
import kr.or.kmi.mis.api.file.repository.FileHistoryRepository;
import kr.or.kmi.mis.api.file.service.FileService;
import kr.or.kmi.mis.api.std.service.StdBcdService;
import kr.or.kmi.mis.api.user.service.InfoService;
import kr.or.kmi.mis.config.SftpClient;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CorpDocServiceImpl implements CorpDocService {

    private final CorpDocMasterRepository corpDocMasterRepository;
    private final CorpDocDetailRepository corpDocDetailRepository;
    private final CorpDocHistoryService corpDocHistoryService;
    private final FileService fileService;
    private final StdBcdService stdBcdService;
    private final SftpClient sftpClient;
    private final FileDetailRepository fileDetailRepository;
    private final FileHistoryRepository fileHistoryRepository;

    @Value("${sftp.remote-directory.corpdoc}")
    private String corpdocRemoteDirectory;

    @Override
    @Transactional
    public void createCorpDocApply(CorpDocRequestDTO corpDocRequestDTO, MultipartFile file) throws Exception {
        String draftId = generateDraftId();

        // 1. CorpDocMaster 저장
        CorpDocMaster corpDocMaster = corpDocRequestDTO.toMasterEntity(draftId);
        corpDocMaster.setRgstrId(corpDocRequestDTO.getDrafterId());
        corpDocMaster.setRgstDt(LocalDateTime.now());
        corpDocMaster = corpDocMasterRepository.save(corpDocMaster);

        // 2. CorpdocDetail 저장
        CorpDocDetail corpDocDetail = corpDocRequestDTO.toDetailEntity(
                corpDocMaster.getDraftId());
        corpDocDetail.setRgstrId(corpDocRequestDTO.getDrafterId());
        corpDocDetail.setRgstDt(LocalDateTime.now());

        corpDocDetailRepository.save(corpDocDetail);

        // 3. File 업로드
        String[] savedFileInfo = handleFileUpload(file);

        FileUploadRequestDTO fileUploadRequestDTO = FileUploadRequestDTO.builder()
                .draftId(draftId)
                .fileName(savedFileInfo[0])
                .filePath(savedFileInfo[1])
                .build();

        fileService.uploadFile(fileUploadRequestDTO);
    }

    private String generateDraftId() {
        Optional<CorpDocMaster> lastCorpdocMasterOpt = corpDocMasterRepository.findTopByOrderByDraftIdDesc();

        if (lastCorpdocMasterOpt.isPresent()) {
            String lastDraftId = lastCorpdocMasterOpt.get().getDraftId();
            int lastIdNum = Integer.parseInt(lastDraftId.substring(2));
            return "cd" + String.format("%010d", lastIdNum + 1);
        } else {
            // TODO: draftId 관련 기준자료 추가 후 수정!!!
            return "cd0000000001";
        }
    }

    @Override
    @Transactional(readOnly = true)
    public CorpDocDetailResponseDTO getCorpDocApply(String draftId) {
        CorpDocDetail corpDocDetail = corpDocDetailRepository.findById(draftId)
                .orElseThrow(() -> new IllegalArgumentException("Not Found"));
        FileDetail fileDetail = fileDetailRepository.findByDraftId(corpDocDetail.getDraftId())
                .orElse(null);
        FileHistory fileHistory = null;
        if (fileDetail != null) {
            fileHistory = fileHistoryRepository.findTopByAttachIdOrderBySeqIdDesc(fileDetail.getAttachId())
                    .orElseThrow(() -> new IllegalArgumentException("Not Found"));
        }
        return CorpDocDetailResponseDTO.of(corpDocDetail, fileHistory);
    }

    @Override
    @Transactional
    public void updateCorpDocApply(String draftId, CorpDocUpdateRequestDTO corpDocUpdateRequestDTO,
                                   MultipartFile file, boolean isFileDeleted) throws Exception {

        // 1. CorpDocMaster 업데이트
        CorpDocMaster corpDocMaster = corpDocMasterRepository.findById(draftId)
                .orElseThrow(() -> new IllegalArgumentException("Not Found"));
        corpDocMaster.setUpdtDt(LocalDateTime.now());
        corpDocMaster.setUpdtrId(corpDocMaster.getDrafterId());
        corpDocMasterRepository.save(corpDocMaster);

        // 2. CorpDocDetail 업데이트
        CorpDocDetail corpDocDetail = corpDocDetailRepository.findById(draftId)
                .orElseThrow(() -> new IllegalArgumentException("Not Found"));

        corpDocHistoryService.createCorpDocHistory(corpDocDetail);

        corpDocDetail.update(corpDocUpdateRequestDTO);
        corpDocDetail.setUpdtrId(corpDocMaster.getDrafter());
        corpDocDetail.setUpdtDt(LocalDateTime.now());

        corpDocDetailRepository.save(corpDocDetail);

        // 3. File 업데이트
        FileDetail fileDetail = fileDetailRepository.findByDraftId(draftId).orElse(null);
        FileHistory fileHistory = null;

        if (fileDetail != null) {
            fileHistory = fileHistoryRepository.findTopByAttachIdOrderBySeqIdDesc(fileDetail.getAttachId())
                    .orElseThrow(() -> new IllegalArgumentException("Not Found"));
        }

        if (file != null && !file.isEmpty()) {
            if (fileHistory != null && fileHistory.getFilePath() != null) {
                String newFileName = file.getOriginalFilename();
                try {
                    String newFilePath = corpdocRemoteDirectory + "/" + newFileName;
                    sftpClient.deleteFile(fileHistory.getFileName(), corpdocRemoteDirectory);
                    sftpClient.uploadFile(file, newFileName, corpdocRemoteDirectory);
                    fileService.updateFile(new FileUploadRequestDTO(corpDocMaster.getDraftId(), newFileName, newFilePath));
                } catch (Exception e) {
                    throw new IOException("SFTP 기존 파일 삭제 실패", e);
                }
            } else {
                String newFileName = file.getOriginalFilename();
                try {
                    String newFilePath = corpdocRemoteDirectory + "/" + newFileName;
                    sftpClient.uploadFile(file, newFileName, corpdocRemoteDirectory);
                    fileService.uploadFile(new FileUploadRequestDTO(corpDocMaster.getDraftId(), newFileName, newFilePath));
                } catch (Exception e) {
                    throw new IOException("SFTP 파일 업로드 실패", e);
                }
            }
        }
        else if (isFileDeleted && fileHistory != null && fileHistory.getFilePath() != null) {
            try {
                sftpClient.deleteFile(fileHistory.getFilePath(), corpdocRemoteDirectory);
                fileDetail.updateUseAt("N");
            } catch (Exception e) {
                throw new IOException("SFTP 파일 삭제 실패", e);
            }
        }
    }

    @Override
    @Transactional
    public void cancelCorpDocApply(String draftId) {

        // 1. CorpDocMaster 삭제 (F)
        CorpDocMaster corpDocMaster = corpDocMasterRepository.findById(draftId)
                .orElseThrow(() -> new IllegalArgumentException("Not Found"));
        corpDocMaster.updateStatus("F");
        corpDocMasterRepository.save(corpDocMaster);

        // 2. FileDetail History 업데이트
        FileDetail fileDetail = fileDetailRepository.findByDraftId(draftId)
                .orElseThrow(() -> new IllegalArgumentException("Not Found"));
        fileDetail.updateUseAt("N");
    }

    private String[] handleFileUpload(MultipartFile file) throws Exception {
        return handleFileUpload(file, null, false);
    }

    private String[] handleFileUpload(MultipartFile file, String existingFilePath, boolean isFileDeleted) throws Exception {
        String fileName = null;
        String filePath = null;

        if (file != null && !file.isEmpty()) {
            fileName = file.getOriginalFilename();
            filePath = corpdocRemoteDirectory + "/" + fileName;
            sftpClient.uploadFile(file, fileName, corpdocRemoteDirectory);

            if (existingFilePath != null) {
                sftpClient.deleteFile(existingFilePath, corpdocRemoteDirectory);
            }
        } else if (isFileDeleted && existingFilePath != null) {
            sftpClient.deleteFile(existingFilePath, corpdocRemoteDirectory);
        }

        return new String[]{fileName, filePath};
    }

    @Override
    @Transactional(readOnly = true)
    public List<CorpDocPendingResponseDTO> getMyPendingList(String userId) {
        return new ArrayList<>(this.getMyCorpDocPendingList(userId));
    }

    @Override
    @Transactional(readOnly = true)
    public List<CorpDocPendingResponseDTO> getPendingList(LocalDateTime startDate, LocalDateTime endDate) {
        List<CorpDocMaster> corpDocMasters = corpDocMasterRepository
                .findAllByStatusAndDraftDateBetweenOrderByDraftDateDesc("A", startDate, endDate);

        return corpDocMasters.stream()
                .map(corpDocMaster -> {
                    CorpDocDetail corpDocDetail = corpDocDetailRepository.findById(corpDocMaster.getDraftId())
                            .orElseThrow(() -> new IllegalArgumentException("Not Found"));
                    CorpDocPendingResponseDTO corpDocPendingResponseDTO = CorpDocPendingResponseDTO.of(corpDocMaster, corpDocDetail);
                    corpDocPendingResponseDTO.setInstNm(stdBcdService.getInstNm(corpDocMaster.getInstCd()));
                    return corpDocPendingResponseDTO;
                }).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<CorpDocMyResponseDTO> getMyCorpDocApply(LocalDateTime startDate, LocalDateTime endDate, String userId) {
        return new ArrayList<>(this.getMyCorpDocList(startDate, endDate, userId));
    }

    @Override
    @Transactional(readOnly = true)
    public List<CorpDocMasterResponseDTO> getCorpDocApply(LocalDateTime startDate, LocalDateTime endDate, String searchType, String keyword) {
        List<CorpDocMaster> corpDocMasters = corpDocMasterRepository
                .findAllByStatusNotAndDraftDateBetweenOrderByDraftDateDesc("F", startDate, endDate);

        if(corpDocMasters == null) {
            corpDocMasters = new ArrayList<>();
        }

        return corpDocMasters.stream()
                .filter(corpDocMaster -> {
                    if (searchType != null && keyword != null) {
                        return switch (searchType) {
                            case "전체" -> corpDocMaster.getTitle().contains(keyword) || corpDocMaster.getDrafter().contains(keyword);
                            case "제목" -> corpDocMaster.getTitle().contains(keyword);
                            case "신청자" -> corpDocMaster.getDrafter().contains(keyword);
                            default -> true;
                        };
                    }
                    return true;
                })
                .map(corpDocMaster -> {
                    CorpDocMasterResponseDTO corpDocMasterResponseDTO = CorpDocMasterResponseDTO.of(corpDocMaster);
                    corpDocMasterResponseDTO.setInstNm(stdBcdService.getInstNm(corpDocMaster.getInstCd()));
                    return corpDocMasterResponseDTO;
                }).toList();
    }

    private List<CorpDocMyResponseDTO> getMyCorpDocList(LocalDateTime startDate, LocalDateTime endDate, String userId) {
        List<CorpDocMaster> corpDocMasterList = corpDocMasterRepository.findByDrafterIdAndDraftDateBetween(userId, startDate, endDate)
                .orElseThrow(() -> new IllegalArgumentException("Not found"));

        return corpDocMasterList.stream()
                .map(CorpDocMyResponseDTO::of).toList();
    }

    public List<CorpDocPendingResponseDTO> getMyCorpDocPendingList(String userId) {
        List<CorpDocMaster> corpDocMasterList = corpDocMasterRepository.findByDrafterIdAndStatus(userId, "A")
                .orElseThrow(() -> new IllegalArgumentException("Not Found"));

        return corpDocMasterList.stream()
                .map(corpDocMaster -> {
                    CorpDocDetail corpDocDetail = corpDocDetailRepository.findById(corpDocMaster.getDraftId())
                            .orElseThrow(() -> new IllegalArgumentException("Division Not Found"));
                    return CorpDocPendingResponseDTO.of(corpDocMaster, corpDocDetail);
                }).toList();
    }
}
