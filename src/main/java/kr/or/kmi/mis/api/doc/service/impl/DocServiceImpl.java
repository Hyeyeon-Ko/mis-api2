package kr.or.kmi.mis.api.doc.service.impl;

import kr.or.kmi.mis.api.authority.model.entity.Authority;
import kr.or.kmi.mis.api.authority.model.request.AuthorityRequestDTO;
import kr.or.kmi.mis.api.authority.repository.AuthorityRepository;
import kr.or.kmi.mis.api.authority.service.AuthorityService;
import kr.or.kmi.mis.api.doc.model.entity.DocDetail;
import kr.or.kmi.mis.api.doc.model.entity.DocMaster;
import kr.or.kmi.mis.api.doc.model.request.ReceiveDocRequestDTO;
import kr.or.kmi.mis.api.doc.model.request.SendDocRequestDTO;
import kr.or.kmi.mis.api.doc.model.request.DocUpdateRequestDTO;
import kr.or.kmi.mis.api.doc.model.response.DocDetailResponseDTO;
import kr.or.kmi.mis.api.doc.model.response.DocMasterResponseDTO;
import kr.or.kmi.mis.api.doc.model.response.DocMyResponseDTO;
import kr.or.kmi.mis.api.doc.model.response.DocPendingResponseDTO;
import kr.or.kmi.mis.api.doc.repository.DocDetailRepository;
import kr.or.kmi.mis.api.doc.repository.DocMasterRepository;
import kr.or.kmi.mis.api.doc.service.DocHistoryService;
import kr.or.kmi.mis.api.doc.service.DocService;
import kr.or.kmi.mis.api.std.model.entity.StdDetail;
import kr.or.kmi.mis.api.std.model.entity.StdGroup;
import kr.or.kmi.mis.api.std.model.request.StdDetailRequestDTO;
import kr.or.kmi.mis.api.std.repository.StdDetailRepository;
import kr.or.kmi.mis.api.std.repository.StdGroupRepository;
import kr.or.kmi.mis.api.std.service.StdBcdService;
import kr.or.kmi.mis.api.std.service.StdDetailService;
import kr.or.kmi.mis.api.user.model.response.InfoDetailResponseDTO;
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
    private final StdGroupRepository stdGroupRepository;
    private final StdDetailRepository stdDetailRepository;
    private final AuthorityRepository authorityRepository;
    private final InfoService infoService;
    private final DocHistoryService docHistoryService;
    private final StdBcdService stdBcdService;
    private final AuthorityService authorityService;
    private final StdDetailService stdDetailService;

    @Value("${file.upload-dir}")
    private String uploadDir;

    @Override
    @Transactional
    public void applyReceiveDoc(ReceiveDocRequestDTO receiveDocRequestDTO, MultipartFile file) throws IOException {

        System.out.println("receiveDocRequestDTO.getReceiver() = " + receiveDocRequestDTO.getReceiver());
        System.out.println("receiveDocRequestDTO.getSender() = " + receiveDocRequestDTO.getSender());
        DocMaster docMaster = receiveDocRequestDTO.toMasterEntity();

        StdGroup stdGroup = stdGroupRepository.findByGroupCd("C002")
                .orElseThrow(() -> new IllegalArgumentException("Not Found"));
        List<StdDetail> stdDetail = stdDetailRepository.findByGroupCdAndEtcItem1(stdGroup, receiveDocRequestDTO.getInstCd())
                .orElseThrow(() -> new IllegalArgumentException("Not Found"));

        docMaster.updateApproverChain(stdDetail.getFirst().getEtcItem3());
        docMaster = docMasterRepository.save(docMaster);

        String[] savedFileInfo = saveFile(file);
        saveReceiveDocDetail(receiveDocRequestDTO, docMaster.getDraftId(), savedFileInfo);
    }

    @Override
    @Transactional
    public void applySendDoc(SendDocRequestDTO sendDocRequestDTO, MultipartFile file) throws IOException {

        // 문서발신 로직
        DocMaster docMaster = sendDocRequestDTO.toMasterEntity();
        docMaster = docMasterRepository.save(docMaster);

        String[] savedFileInfo = saveFile(file);
        saveSendDocDetail(sendDocRequestDTO, docMaster.getDraftId(), savedFileInfo);

        // ADMIN 권한 부여
        List<Authority> authorityList = authorityRepository.findAllByDeletedtIsNull();

        String firstApproverId = sendDocRequestDTO.getApproverIds().getFirst();
        InfoDetailResponseDTO infoDetailResponseDTO = infoService.getUserInfoDetail(firstApproverId);
        String userNm = infoDetailResponseDTO.getUserName();
        String instNm = infoDetailResponseDTO.getInstNm();
        String deptNm = infoDetailResponseDTO.getDeptNm();

        boolean authorityExists = authorityList.stream()
                .anyMatch(authority -> authority.getUserId().equals(firstApproverId));

        if (!authorityExists) {
            AuthorityRequestDTO requestDTO = AuthorityRequestDTO.builder()
                    .userId(firstApproverId)
                    .userNm(userNm)
                    .userRole("ADMIN")
                    .detailRole(null)
                    .build();

            authorityService.addAdmin(requestDTO);

            // 사이드바 권한 부여
            StdDetailRequestDTO stdDetailRequestDTO = StdDetailRequestDTO.builder()
                    .detailCd(firstApproverId)
                    .groupCd("B002")
                    .detailNm(instNm + " " + deptNm)
                    .etcItem1(userNm)
                    .etcItem2("D-2")
                    .build();

            stdDetailService.addInfo(stdDetailRequestDTO);
        }
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
                    return DocMyResponseDTO.of(docMaster, docDetail.getDivision(), infoService);
                }).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<DocPendingResponseDTO> getMyDocPendingList(String userId) {
        return new ArrayList<>(this.getMyDocPendingMasterList(userId));
    }

    @Override
    @Transactional(readOnly = true)
    public List<DocMasterResponseDTO> getDocApplyByInstCd(String instCd, String userId) {
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
    public List<DocPendingResponseDTO> getDocPendingList(String instCd, String userId) {
        List<DocMaster> docMasters = docMasterRepository
                .findAllByStatusAndInstCdOrderByDraftDateDesc("A", instCd);

        return docMasters.stream()
                .filter(docMaster -> {
                    String[] approverChainArray = docMaster.getApproverChain().split(", ");
                    int currentIndex = docMaster.getCurrentApproverIndex();

                    return currentIndex < approverChainArray.length && approverChainArray[currentIndex].equals(userId);
                })
                .map(docMaster -> {
                    DocDetail docDetail = docDetailRepository.findById(docMaster.getDraftId())
                            .orElseThrow(() -> new IllegalArgumentException("Division Not Found"));

                    DocPendingResponseDTO docPendingResponseDTO = DocPendingResponseDTO.of(docMaster, docDetail.getDivision());
                    docPendingResponseDTO.setInstNm(stdBcdService.getInstNm(docMaster.getInstCd()));

                    return docPendingResponseDTO;
                }).toList();
    }

    public List<DocPendingResponseDTO> getMyDocPendingMasterList(String userId) {
        List<DocMaster> docMasterList = docMasterRepository.findByDrafterIdAndStatusAndCurrentApproverIndex(userId, "A", 0)
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

    private void saveSendDocDetail(SendDocRequestDTO sendDocRequestDTO, Long draftId, String[] savedFileInfo) {
        DocDetail docDetail = sendDocRequestDTO.toDetailEntity(draftId, savedFileInfo[0], savedFileInfo[1]);
        docDetailRepository.save(docDetail);
    }

    private void saveReceiveDocDetail(ReceiveDocRequestDTO receiveDocRequestDTO, Long draftId, String[] savedFileInfo) {
        DocDetail docDetail = receiveDocRequestDTO.toDetailEntity(draftId, savedFileInfo[0], savedFileInfo[1]);
        docDetailRepository.save(docDetail);
    }

    private void updateDocDetail(Object docRequestOrUpdateDTO, Long draftId, String[] savedFileInfo) {
        DocDetail existingDocDetail = docDetailRepository.findById(draftId)
                .orElseThrow(() -> new IllegalArgumentException("Not Found"));

        if (docRequestOrUpdateDTO instanceof SendDocRequestDTO sendDocRequestDTO) {
            existingDocDetail.update(sendDocRequestDTO, savedFileInfo[0], savedFileInfo[1]);
        } else if (docRequestOrUpdateDTO instanceof DocUpdateRequestDTO docUpdateRequestDTO) {
            existingDocDetail.updateFile(docUpdateRequestDTO, savedFileInfo[0], savedFileInfo[1]);
        }

        docDetailRepository.save(existingDocDetail);
    }
}
