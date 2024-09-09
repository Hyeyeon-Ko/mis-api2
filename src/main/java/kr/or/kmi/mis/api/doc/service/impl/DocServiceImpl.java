package kr.or.kmi.mis.api.doc.service.impl;

import kr.or.kmi.mis.api.doc.model.entity.DocDetail;
import kr.or.kmi.mis.api.doc.model.entity.DocMaster;
import kr.or.kmi.mis.api.doc.model.request.DocRequestDTO;
import kr.or.kmi.mis.api.doc.model.request.DocUpdateRequestDTO;
import kr.or.kmi.mis.api.doc.model.response.DocDetailResponseDTO;
import kr.or.kmi.mis.api.doc.model.response.DocMasterResponseDTO;
import kr.or.kmi.mis.api.doc.model.response.DocMyResponseDTO;
import kr.or.kmi.mis.api.doc.model.response.DocPendingResponseDTO;
import kr.or.kmi.mis.api.doc.repository.DocDetailRepository;
import kr.or.kmi.mis.api.doc.repository.DocMasterRepository;
import kr.or.kmi.mis.api.doc.service.DocHistoryService;
import kr.or.kmi.mis.api.doc.service.DocService;
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
public class DocServiceImpl implements DocService {

    private final DocMasterRepository docMasterRepository;
    private final DocDetailRepository docDetailRepository;
    private final DocHistoryService docHistoryService;
    private final StdBcdService stdBcdService;

    @Value("${file.upload-dir}")
    private String uploadDir;

    @Override
    @Transactional
    public void applyDoc(DocRequestDTO docRequestDTO, MultipartFile file) throws IOException {
        DocMaster docMaster = docRequestDTO.toMasterEntity();
        docMaster = docMasterRepository.save(docMaster);

        String[] savedFileInfo = saveFile(file);
        saveDocDetail(docRequestDTO, docMaster.getDraftId(), savedFileInfo);
    }

    @Override
    @Transactional
    public void updateDocApply(Long draftId, DocUpdateRequestDTO docUpdateRequestDTO, MultipartFile file, boolean isFileDeleted) throws IOException {
        DocMaster docMaster = docMasterRepository.findById(draftId)
                .orElseThrow(() -> new IllegalArgumentException("Not Found"));

        DocDetail docDetailInfo = docDetailRepository.findById(draftId)
                .orElseThrow(() -> new IllegalArgumentException("Not Found"));

        docHistoryService.createDocHistory(docDetailInfo);

        String[] savedFileInfo;
        if (file != null) {
            savedFileInfo = saveFile(file, docDetailInfo.getFilePath());
        } else if (isFileDeleted) {
            savedFileInfo = new String[]{null, null};
            if (docDetailInfo.getFilePath() != null) {
                Path oldFilePath = Paths.get(docDetailInfo.getFilePath());
                Files.deleteIfExists(oldFilePath);
            }
        } else {
            savedFileInfo = new String[]{docDetailInfo.getFileName(), docDetailInfo.getFilePath()};
        }

        updateDocDetail(docUpdateRequestDTO, draftId, savedFileInfo);

        docMaster.setUpdtDt(new Timestamp(System.currentTimeMillis()));
        docMaster.setUpdtrId(docMaster.getDrafterId());
    }

    @Override
    @Transactional
    public void cancelDocApply(Long draftId) {
        DocMaster docMaster = docMasterRepository.findById(draftId)
                .orElseThrow(() -> new IllegalArgumentException("Not Found"));

        docMaster.updateStatus("F");
    }

    @Override
    @Transactional(readOnly = true)
    public DocDetailResponseDTO getDoc(Long draftId) {
        DocMaster docMaster = docMasterRepository.findById(draftId)
                .orElseThrow(() -> new IllegalArgumentException("Not Found"));

        DocDetail docDetail = docDetailRepository.findById(draftId)
                .orElseThrow(() -> new IllegalArgumentException("Not Found"));

        return DocDetailResponseDTO.of(docMaster, docDetail);
    }

    @Override
    @Transactional(readOnly = true)
    public List<DocMyResponseDTO> getMyDocApply(String userId) {
        return new ArrayList<>(this.getMyDocMasterList(userId));
    }

    public List<DocMyResponseDTO> getMyDocMasterList(String userId) {
        List<DocMaster> docMasterList = docMasterRepository.findByDrafterId(userId)
                .orElseThrow(() -> new IllegalArgumentException("Not Found"));

        return docMasterList.stream()
                .map(docMaster -> {
                    DocDetail docDetail = docDetailRepository.findById(docMaster.getDraftId())
                            .orElseThrow(() -> new IllegalArgumentException("Division Not Found"));
                    return DocMyResponseDTO.of(docMaster, docDetail.getDivision());
                }).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<DocPendingResponseDTO> getMyDocPendingList(String userId) {
        return new ArrayList<>(this.getMyDocPendingMasterList(userId));
    }

    @Override
    @Transactional(readOnly = true)
    public List<DocMasterResponseDTO> getDocApplyByInstCd(String instCd) {
        List<DocMaster> docMasters = docMasterRepository.findAllByStatusNotAndInstCdOrderByDraftDateDesc("F", instCd);

        if (docMasters == null) {
            docMasters = new ArrayList<>();
        }

        return docMasters.stream()
                .map(docMaster -> {
                    DocDetail docDetail = docDetailRepository.findById(docMaster.getDraftId())
                            .orElseThrow(() -> new IllegalArgumentException("Division Not Found"));
                    DocMasterResponseDTO docMasterResponseDTO = DocMasterResponseDTO.of(docMaster, docDetail.getDivision());
                    docMasterResponseDTO.setInstNm(stdBcdService.getInstNm(docMaster.getInstCd()));
                    return docMasterResponseDTO;
                })
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<DocPendingResponseDTO> getDocPendingList(String instCd) {
        List<DocMaster> docMasters = docMasterRepository
                .findAllByStatusAndInstCdOrderByDraftDateDesc("A", instCd);

        return docMasters.stream()
                .map(docMaster -> {
                    DocDetail docDetail = docDetailRepository.findById(docMaster.getDraftId())
                            .orElseThrow(() -> new IllegalArgumentException("Division Not Found"));
                    DocPendingResponseDTO docPendingResponseDTO = DocPendingResponseDTO.of(docMaster, docDetail.getDivision());
                    docPendingResponseDTO.setInstNm(stdBcdService.getInstNm(docMaster.getInstCd()));
                    return docPendingResponseDTO;
                }).toList();
    }

    public List<DocPendingResponseDTO> getMyDocPendingMasterList(String userId) {
        List<DocMaster> docMasterList = docMasterRepository.findByDrafterIdAndStatus(userId, "A")
                .orElseThrow(() -> new IllegalArgumentException("Not Found"));

        return docMasterList.stream()
                .map(docMaster -> {
                    DocDetail docDetail = docDetailRepository.findById(docMaster.getDraftId())
                            .orElseThrow(() -> new IllegalArgumentException("Division Not Found"));
                    return DocPendingResponseDTO.of(docMaster, docDetail.getDivision());
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

    private void saveDocDetail(DocRequestDTO docRequestDTO, Long draftId, String[] savedFileInfo) {
        DocDetail docDetail = docRequestDTO.toDetailEntity(draftId, savedFileInfo[0], savedFileInfo[1]);
        docDetailRepository.save(docDetail);
    }

    private void updateDocDetail(Object docRequestOrUpdateDTO, Long draftId, String[] savedFileInfo) {
        DocDetail existingDocDetail = docDetailRepository.findById(draftId)
                .orElseThrow(() -> new IllegalArgumentException("Not Found"));

        if (docRequestOrUpdateDTO instanceof DocRequestDTO docRequestDTO) {
            existingDocDetail.update(docRequestDTO, savedFileInfo[0], savedFileInfo[1]);
        } else if (docRequestOrUpdateDTO instanceof DocUpdateRequestDTO docUpdateRequestDTO) {
            existingDocDetail.updateFile(docUpdateRequestDTO, savedFileInfo[0], savedFileInfo[1]);
        }

        docDetailRepository.save(existingDocDetail);
    }
}
