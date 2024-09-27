package kr.or.kmi.mis.api.corpdoc.service.impl;

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
import kr.or.kmi.mis.api.file.model.request.FileUploadRequestDTO;
import kr.or.kmi.mis.api.file.repository.FileDetailRepository;
import kr.or.kmi.mis.api.file.service.FileHistorySevice;
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
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CorpDocServiceImpl implements CorpDocService {

    private final CorpDocMasterRepository corpDocMasterRepository;
    private final CorpDocDetailRepository corpDocDetailRepository;
    private final CorpDocHistoryService corpDocHistoryService;
    private final FileService fileService;
    private final FileHistorySevice fileHistorySevice;
    private final StdBcdService stdBcdService;
    private final InfoService infoService;
    private final SftpClient sftpClient;
    private final FileDetailRepository fileDetailRepository;

    @Value("${sftp.remote-directory.corpdoc}")
    private String corpdocRemoteDirectory;

    @Override
    @Transactional
    public void createCorpDocApply(CorpDocRequestDTO corpDocRequestDTO, MultipartFile file) throws Exception {

        // 1. CorpDocMaster 저장
        CorpDocMaster corpDocMaster = corpDocRequestDTO.toMasterEntity();
        corpDocMaster.setRgstrId(corpDocRequestDTO.getDrafterId());
        corpDocMaster.setRgstDt(new Timestamp(System.currentTimeMillis()));
        corpDocMaster = corpDocMasterRepository.save(corpDocMaster);

        // 2. CorpdocDetail 저장
        CorpDocDetail corpDocDetail = corpDocRequestDTO.toDetailEntity(
                corpDocMaster.getDraftId());
        corpDocDetail.setRgstrId(corpDocRequestDTO.getDrafterId());
        corpDocDetail.setRgstDt(new Timestamp(System.currentTimeMillis()));

        corpDocDetailRepository.save(corpDocDetail);

        // 3. File 업로드
        String[] savedFileInfo = handleFileUpload(file);

        FileUploadRequestDTO fileUploadRequestDTO = FileUploadRequestDTO.builder()
                .draftId(corpDocDetail.getDraftId())
                .drafter(corpDocMaster.getDrafter())
                .docType("B")
                .fileName(savedFileInfo[0])
                .filePath(savedFileInfo[1])
                .build();

        fileService.uploadFile(fileUploadRequestDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public CorpDocDetailResponseDTO getCorpDocApply(Long draftId) {
        CorpDocDetail corpDocDetail = corpDocDetailRepository.findById(draftId)
                .orElseThrow(() -> new IllegalArgumentException("Not Found"));
        FileDetail fileDetail = fileDetailRepository.findByDraftIdAndDocType(draftId, "B")
                .orElseThrow(() -> new IllegalArgumentException("Not Found"));
        return CorpDocDetailResponseDTO.of(corpDocDetail, fileDetail);
    }

    @Override
    @Transactional
    public void updateCorpDocApply(Long draftId, CorpDocUpdateRequestDTO corpDocUpdateRequestDTO,
                                   MultipartFile file, boolean isFileDeleted) throws Exception {

        // 1. CorpDocMaster 업데이트
        CorpDocMaster corpDocMaster = corpDocMasterRepository.findById(draftId)
                .orElseThrow(() -> new IllegalArgumentException("Not Found"));
        corpDocMaster.setUpdtDt(new Timestamp(System.currentTimeMillis()));
        corpDocMaster.setUpdtrId(corpDocMaster.getDrafterId());
        corpDocMasterRepository.save(corpDocMaster);

        // 2. CorpDocDetail 업데이트
        CorpDocDetail corpDocDetail = corpDocDetailRepository.findById(draftId)
                .orElseThrow(() -> new IllegalArgumentException("Not Found"));

        corpDocHistoryService.createCorpDocHistory(corpDocDetail);

        corpDocDetail.update(corpDocUpdateRequestDTO);
        corpDocDetail.setUpdtrId(corpDocMaster.getDrafter());
        corpDocDetail.setUpdtDt(new Timestamp(System.currentTimeMillis()));

        corpDocDetailRepository.save(corpDocDetail);

        //3. FileDetail 업데이트
        FileDetail fileDetail = fileDetailRepository.findByDraftIdAndDocType(draftId, "B")
                .orElse(null);

        if (fileDetail != null) fileHistorySevice.createFileHistory(fileDetail, "A");

        assert fileDetail != null;
        String[] savedFileInfo = {fileDetail.getFileName(), fileDetail.getFilePath()};

        if (file != null && !file.isEmpty()) {
            if (savedFileInfo[1] != null) {
                try {
                    sftpClient.deleteFile(savedFileInfo[1], corpdocRemoteDirectory);
                } catch (Exception e) {
                    throw new IOException("SFTP 기존 파일 삭제 실패", e);
                }
            }

            String newFileName = file.getOriginalFilename();
            try {
                sftpClient.uploadFile(file, newFileName, corpdocRemoteDirectory);
                savedFileInfo[0] = newFileName;
                savedFileInfo[1] = corpdocRemoteDirectory + "/" + newFileName;
            } catch (Exception e) {
                throw new IOException("SFTP 파일 업로드 실패", e);
            }
        }
        else if (isFileDeleted && savedFileInfo[1] != null) {
            try {
                sftpClient.deleteFile(savedFileInfo[1], corpdocRemoteDirectory);
                savedFileInfo[0] = null;
                savedFileInfo[1] = null;
            } catch (Exception e) {
                throw new IOException("SFTP 파일 삭제 실패", e);
            }
        }

        fileDetail.updateFileInfo(savedFileInfo[0], savedFileInfo[1]);
        fileDetail.setUpdtDt(new Timestamp(System.currentTimeMillis()));
        fileDetail.setUpdtrId(corpDocMaster.getDrafterId());
        fileDetailRepository.save(fileDetail);
    }

    @Override
    @Transactional
    public void cancelCorpDocApply(Long draftId) {

        // 1. CorpDocMaster 삭제 (F)
        CorpDocMaster corpDocMaster = corpDocMasterRepository.findById(draftId)
                .orElseThrow(() -> new IllegalArgumentException("Not Found"));
        corpDocMaster.updateStatus("F");
        corpDocMasterRepository.save(corpDocMaster);

        // 2. FileDetail History 업데이트
        FileDetail fileDetail = fileDetailRepository.findByDraftIdAndDocType(draftId, "B")
                .orElseThrow(() -> new IllegalArgumentException("Not Found"));
        fileHistorySevice.createFileHistory(fileDetail, "B");
        fileDetailRepository.delete(fileDetail);
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
    public List<CorpDocPendingResponseDTO> getPendingList(Timestamp startDate, Timestamp endDate) {
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
    public List<CorpDocMyResponseDTO> getMyCorpDocApply(Timestamp startDate, Timestamp endDate, String userId) {
        return new ArrayList<>(this.getMyCorpDocList(startDate, endDate, userId));
    }

    @Override
    @Transactional(readOnly = true)
    public List<CorpDocMasterResponseDTO> getCorpDocApply(Timestamp startDate, Timestamp endDate, String searchType, String keyword) {
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

    private List<CorpDocMyResponseDTO> getMyCorpDocList(Timestamp startDate, Timestamp endDate, String userId) {
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
