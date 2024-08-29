package kr.or.kmi.mis.api.seal.service.impl;

import kr.or.kmi.mis.api.seal.model.entity.SealRegisterDetail;
import kr.or.kmi.mis.api.seal.model.request.SealRegisterRequestDTO;
import kr.or.kmi.mis.api.seal.model.request.SealUpdateRequestDTO;
import kr.or.kmi.mis.api.seal.model.response.SealDetailResponseDTO;
import kr.or.kmi.mis.api.seal.repository.SealRegisterDetailRepository;
import kr.or.kmi.mis.api.seal.service.SealRegisterHistoryService;
import kr.or.kmi.mis.api.seal.service.SealRegisterService;
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
public class SealRegisterServiceImpl implements SealRegisterService {

    private final SealRegisterDetailRepository sealRegisterDetailRepository;
    private final SealRegisterHistoryService sealRegisterHistoryService;

    @Value("${file.upload-dir}")
    private String uploadDir;

    @Transactional
    public void registerSeal(SealRegisterRequestDTO sealRegisterRequestDTO, MultipartFile sealImage) throws IOException {
        String[] savedFileInfo = saveFile(sealImage, null);

        SealRegisterDetail sealRegisterDetail = sealRegisterRequestDTO.toDetailEntity(savedFileInfo[0], savedFileInfo[1]);
        sealRegisterDetail.setRgstrId(sealRegisterRequestDTO.getDrafterId());
        sealRegisterDetail.setRgstDt(new Timestamp(System.currentTimeMillis()));
        sealRegisterDetailRepository.save(sealRegisterDetail);
    }

    @Override
    @Transactional
    public void updateSeal(Long draftId, SealUpdateRequestDTO sealUpdateRequestDTO, MultipartFile sealImage, boolean isFileDeleted) throws IOException {
        SealRegisterDetail sealRegisterDetail = sealRegisterDetailRepository.findById(draftId)
                .orElseThrow(() -> new IllegalArgumentException("Not Found"));

        sealRegisterHistoryService.createSealRegisterHistory(sealRegisterDetail);

        String[] savedFileInfo;
        if (sealImage != null) {
            savedFileInfo = saveFile(sealImage, sealRegisterDetail.getSealImagePath());
        } else if (isFileDeleted) {
            savedFileInfo = new String[]{null, null};
            if (sealRegisterDetail.getSealImagePath() != null) {
                Path filePath = Paths.get(sealRegisterDetail.getSealImagePath());
                Files.deleteIfExists(filePath);
            }
        } else {
            savedFileInfo = new String[]{sealRegisterDetail.getSealImage(), sealRegisterDetail.getSealImagePath()};
        }

        updateSealRegistrationDetail(sealUpdateRequestDTO, draftId, savedFileInfo);
    }

    private void updateSealRegistrationDetail(Object sealRequestOrUpdateDTO, Long draftId, String[] savedFileInfo) {
        SealRegisterDetail existingSealRegisterDetail = sealRegisterDetailRepository.findById(draftId)
                .orElseThrow(() -> new IllegalArgumentException("Not Found"));

        if (sealRequestOrUpdateDTO instanceof SealRegisterRequestDTO sealRegisterRequestDTO) {
            existingSealRegisterDetail.update(sealRegisterRequestDTO, savedFileInfo[0], savedFileInfo[1]);
        } else if (sealRequestOrUpdateDTO instanceof SealUpdateRequestDTO sealUpdateRequestDTO) {
            existingSealRegisterDetail.updateFile(sealUpdateRequestDTO, savedFileInfo[0], savedFileInfo[1]);
        }

        sealRegisterDetailRepository.save(existingSealRegisterDetail);
    }

    @Override
    @Transactional
    public void deleteSeal(Long draftId) {
        sealRegisterDetailRepository.deleteById(draftId);
    }

    @Override
    public SealDetailResponseDTO getSealDetail(Long draftId) {
        SealRegisterDetail sealRegisterDetail = sealRegisterDetailRepository.findById(draftId)
                .orElseThrow(() -> new IllegalArgumentException("Not Found"));

        return SealDetailResponseDTO.of(sealRegisterDetail);
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
