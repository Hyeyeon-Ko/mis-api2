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
import kr.or.kmi.mis.api.std.service.StdBcdService;
import kr.or.kmi.mis.api.user.service.InfoService;
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
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CorpDocServiceImpl implements CorpDocService {

    private final CorpDocMasterRepository corpDocMasterRepository;
    private final CorpDocDetailRepository corpDocDetailRepository;
    private final CorpDocHistoryService corpDocHistoryService;
    private final StdBcdService stdBcdService;
    private final InfoService infoService;

    @Value("${file.upload-dir}")
    private String uploadDir;

    @Override
    @Transactional
    public void createCorpDocApply(CorpDocRequestDTO corpDocRequestDTO, MultipartFile file) throws IOException {
        CorpDocMaster corpDocMaster = corpDocRequestDTO.toMasterEntity();
        corpDocMaster.setRgstrId(corpDocRequestDTO.getDrafterId());
        corpDocMaster.setRgstDt(new Timestamp(System.currentTimeMillis()));
        corpDocMaster = corpDocMasterRepository.save(corpDocMaster);

        String[] savedFileInfo = saveFile(file);
        CorpDocDetail corpDocDetail = corpDocRequestDTO.toDetailEntity(
                corpDocMaster.getDraftId(), savedFileInfo[0], savedFileInfo[1]);
        corpDocDetail.setRgstrId(corpDocRequestDTO.getDrafterId());
        corpDocDetail.setRgstDt(new Timestamp(System.currentTimeMillis()));
        corpDocDetailRepository.save(corpDocDetail);
    }

    @Override
    @Transactional(readOnly = true)
    public CorpDocDetailResponseDTO getCorpDocApply(Long draftId) {
        CorpDocDetail corpDocDetail = corpDocDetailRepository.findById(draftId)
                .orElseThrow(() -> new IllegalArgumentException("Not Found"));
        return CorpDocDetailResponseDTO.of(corpDocDetail);
    }

    @Override
    @Transactional
    public void updateCorpDocApply(Long draftId, CorpDocUpdateRequestDTO corpDocUpdateRequestDTO,
                                   MultipartFile file, boolean isFileDeleted) throws IOException {
        CorpDocDetail corpDocDetail = corpDocDetailRepository.findById(draftId)
                .orElseThrow(() -> new IllegalArgumentException("Not Found"));

        corpDocHistoryService.createCorpDocHistory(corpDocDetail);

        String[] savedFileInfo;
        if (file != null) {
            savedFileInfo = saveFile(file, corpDocDetail.getFilePath());
        } else if (isFileDeleted) {
            savedFileInfo = new String[]{null, null};
            if (corpDocDetail.getFilePath() != null) {
                Path oldFilePath = Paths.get(corpDocDetail.getFilePath());
                Files.deleteIfExists(oldFilePath);
            }
        } else {
            savedFileInfo = new String[]{corpDocDetail.getFileName(), corpDocDetail.getFilePath()};
        }

        corpDocDetail.update(corpDocUpdateRequestDTO, savedFileInfo[0], savedFileInfo[1]);
        corpDocDetail.setUpdtrId(infoService.getUserInfo().getUserName());
        corpDocDetail.setUpdtDt(new Timestamp(System.currentTimeMillis()));

        corpDocDetailRepository.save(corpDocDetail);
    }

    @Override
    @Transactional
    public void cancelCorpDocApply(Long draftId) {
        CorpDocMaster corpDocMaster = corpDocMasterRepository.findById(draftId)
                .orElseThrow(() -> new IllegalArgumentException("Not Found"));

        corpDocMaster.updateStatus("F");
        corpDocMasterRepository.save(corpDocMaster);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CorpDocPendingResponseDTO> getMyPendingList(String userId) {
        return new ArrayList<>(this.getMyCorpDocPendingList(userId));
    }

    @Override
    @Transactional(readOnly = true)
    public List<CorpDocPendingResponseDTO> getPendingList() {
        List<CorpDocMaster> corpDocMasters = corpDocMasterRepository
                .findAllByStatusOrderByDraftDateDesc("A");

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
    public List<CorpDocMyResponseDTO> getMyCorpDocApply(String userId) {
        return new ArrayList<>(this.getMyCorpDocList(userId));
    }

    @Override
    @Transactional(readOnly = true)
    public List<CorpDocMasterResponseDTO> getCorpDocApply() {
        List<CorpDocMaster> corpDocMasters = corpDocMasterRepository
                .findAllByStatusNotOrderByDraftDateDesc("F");

        if(corpDocMasters == null) {
            corpDocMasters = new ArrayList<>();
        }

        return corpDocMasters.stream()
                .map(corpDocMaster -> {
                    CorpDocMasterResponseDTO corpDocMasterResponseDTO = CorpDocMasterResponseDTO.of(corpDocMaster);
                    corpDocMasterResponseDTO.setInstNm(stdBcdService.getInstNm(corpDocMaster.getInstCd()));
                    return corpDocMasterResponseDTO;
                })
                .toList();
    }

    private List<CorpDocMyResponseDTO> getMyCorpDocList(String userId) {
        List<CorpDocMaster> corpDocMasterList = corpDocMasterRepository.findByDrafterId(userId)
                .orElseThrow(() -> new IllegalArgumentException("Not found"));

        return corpDocMasterList.stream()
                .map(corpDocMaster -> {
                    CorpDocDetail corpDocDetail = corpDocDetailRepository.findById(corpDocMaster.getDraftId())
                            .orElseThrow(() -> new IllegalArgumentException("Not found"));
                    return CorpDocMyResponseDTO.of(corpDocMaster);
                }).toList();
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
