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

        // 1. CorpDocDetail 업데이트
        CorpDocMaster corpDocMaster = corpDocMasterRepository.findById(draftId)
                .orElseThrow(() -> new IllegalArgumentException("Not Found"));

        CorpDocDetail corpDocDetail = corpDocDetailRepository.findById(draftId)
                .orElseThrow(() -> new IllegalArgumentException("Not Found"));

        corpDocHistoryService.createCorpDocHistory(corpDocDetail);

        corpDocDetail.update(corpDocUpdateRequestDTO);
        corpDocDetail.setUpdtrId(infoService.getUserInfo().getUserName());
        corpDocDetail.setUpdtDt(new Timestamp(System.currentTimeMillis()));

        corpDocDetailRepository.save(corpDocDetail);

        // 2. FileDetail 업데이트
        FileDetail fileDetail = fileDetailRepository.findByDraftIdAndDocType(draftId, "B")
                .orElseThrow(() -> new IllegalArgumentException("Not Found"));

        fileHistorySevice.createFileHistory(fileDetail, "A");

        String[] savedFileInfo = handleFileUpload(file, fileDetail.getFilePath(), isFileDeleted);

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
