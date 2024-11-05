package kr.or.kmi.mis.api.doc.service.impl;

import kr.or.kmi.mis.api.apply.model.request.ApplyRequestDTO;
import kr.or.kmi.mis.api.authority.model.request.AuthorityRequestDTO;
import kr.or.kmi.mis.api.authority.repository.AuthorityRepository;
import kr.or.kmi.mis.api.authority.service.AuthorityService;
import kr.or.kmi.mis.api.doc.model.entity.DocDetail;
import kr.or.kmi.mis.api.doc.model.entity.DocMaster;
import kr.or.kmi.mis.api.doc.model.request.DocUpdateRequestDTO;
import kr.or.kmi.mis.api.doc.model.request.ReceiveDocRequestDTO;
import kr.or.kmi.mis.api.doc.model.request.SendDocRequestDTO;
import kr.or.kmi.mis.api.doc.model.response.DocDetailResponseDTO;
import kr.or.kmi.mis.api.doc.model.response.DocMasterResponseDTO;
import kr.or.kmi.mis.api.doc.model.response.DocMyResponseDTO;
import kr.or.kmi.mis.api.doc.model.response.DocPendingResponseDTO;
import kr.or.kmi.mis.api.doc.repository.DocApplyQueryRepository;
import kr.or.kmi.mis.api.doc.repository.DocDetailRepository;
import kr.or.kmi.mis.api.doc.repository.DocMasterRepository;
import kr.or.kmi.mis.api.doc.repository.DocPendingQueryRepository;
import kr.or.kmi.mis.api.doc.service.DocHistoryService;
import kr.or.kmi.mis.api.doc.service.DocService;
import kr.or.kmi.mis.api.file.model.entity.FileDetail;
import kr.or.kmi.mis.api.file.model.entity.FileHistory;
import kr.or.kmi.mis.api.file.model.request.FileUploadRequestDTO;
import kr.or.kmi.mis.api.file.repository.FileDetailRepository;
import kr.or.kmi.mis.api.file.repository.FileHistoryRepository;
import kr.or.kmi.mis.api.file.service.FileService;
import kr.or.kmi.mis.api.std.model.entity.StdDetail;
import kr.or.kmi.mis.api.std.model.entity.StdGroup;
import kr.or.kmi.mis.api.std.model.request.StdDetailRequestDTO;
import kr.or.kmi.mis.api.std.repository.StdDetailRepository;
import kr.or.kmi.mis.api.std.repository.StdGroupRepository;
import kr.or.kmi.mis.api.std.service.StdBcdService;
import kr.or.kmi.mis.api.std.service.StdDetailService;
import kr.or.kmi.mis.api.std.service.StdGroupService;
import kr.or.kmi.mis.api.user.model.response.InfoDetailResponseDTO;
import kr.or.kmi.mis.api.user.service.InfoService;
import kr.or.kmi.mis.cmm.model.request.PostSearchRequestDTO;
import kr.or.kmi.mis.config.SftpClient;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import utils.SearchUtils;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static kr.or.kmi.mis.api.confirm.service.Impl.DocConfirmServiceImpl.createDocId;

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
    private final FileService fileService;
    private final StdBcdService stdBcdService;
    private final AuthorityService authorityService;
    private final StdDetailService stdDetailService;
    private final StdGroupService stdGroupService;

    private final SftpClient sftpClient;
    private final FileDetailRepository fileDetailRepository;
    private final FileHistoryRepository fileHistoryRepository;

    private final DocApplyQueryRepository docApplyQueryRepository;
    private final DocPendingQueryRepository docPendingQueryRepository;

    @Value("${sftp.remote-directory.doc}")
    private String docRemoteDirectory;

    @Override
    @Transactional
    public void applyReceiveDoc(ReceiveDocRequestDTO receiveDocRequestDTO, MultipartFile file) throws IOException {
        String draftId = generateDraftId();

        // 1. DocMaster 저장
        DocMaster docMaster = receiveDocRequestDTO.toMasterEntity(draftId, "A");

        StdGroup stdGroup = stdGroupRepository.findByGroupCd("B005")
                .orElseThrow(() -> new IllegalArgumentException("Not Found"));
        List<StdDetail> stdDetail = stdDetailRepository.findByGroupCdAndEtcItem1(stdGroup, receiveDocRequestDTO.getInstCd())
                .orElseThrow(() -> new IllegalArgumentException("Not Found"));

        docMaster.updateApproverChain(stdDetail.getFirst().getEtcItem3());
        docMaster = docMasterRepository.save(docMaster);

        // 2. DocDetail 저장
        DocDetail docDetail = receiveDocRequestDTO.toDetailEntity(docMaster.getDraftId());
        docDetailRepository.save(docDetail);

        // 3. File 업로드
        getCenterNm(receiveDocRequestDTO, file, docMaster);
    }

    @Override
    @Transactional
    public void applyReceiveDocByLeader(ReceiveDocRequestDTO receiveDocRequestDTO, MultipartFile file) throws IOException {

        // 1. DocMaster, DocDetail 저장
        DocMaster docMaster = saveDocMasterAndDetail(receiveDocRequestDTO);

        // 2. File 업로드
        getCenterNm(receiveDocRequestDTO, file, docMaster);
    }

    @Override
    @Transactional
    public void applySendDoc(SendDocRequestDTO sendDocRequestDTO, MultipartFile file) throws IOException {
        String draftId = generateDraftId();

        // 1. DocMaster 저장
        DocMaster docMaster = sendDocRequestDTO.toMasterEntity(draftId, "A");
        docMaster = docMasterRepository.save(docMaster);

        // 2. DocDetail 저장
        DocDetail docDetail = sendDocRequestDTO.toDetailEntity(docMaster.getDraftId());
        docDetailRepository.save(docDetail);

        String firstApproverId = sendDocRequestDTO.getApproverIds().getFirst();
        InfoDetailResponseDTO infoDetailResponseDTO = infoService.getUserInfoDetail(firstApproverId);

        grantAdminAuthorityIfAbsent(firstApproverId, infoDetailResponseDTO);
        updateSidebarPermissionsIfNeeded(firstApproverId);

        // 3. File 업로드
        getCenterNm(sendDocRequestDTO, file, docMaster);
    }

    @Override
    @Transactional
    public void applySendDocByLeader(SendDocRequestDTO sendDocRequestDTO, MultipartFile file) throws IOException {

        // 1. DocMaster, DocDetail 저장
        DocMaster docMaster = saveDocMasterAndDetail(sendDocRequestDTO);

        // 2. File 업로드
        getCenterNm(sendDocRequestDTO, file, docMaster);
    }

    private <T> DocMaster saveDocMasterAndDetail(T requestDTO) {
        String draftId = generateDraftId();

        // 1. DocMaster 저장
        DocMaster docMaster = (requestDTO instanceof ReceiveDocRequestDTO)
                ? ((ReceiveDocRequestDTO) requestDTO).toMasterEntity(draftId, "E")
                : ((SendDocRequestDTO) requestDTO).toMasterEntity(draftId, "E");
        docMaster.updateRespondDate(LocalDateTime.now());
        docMaster = docMasterRepository.save(docMaster);

        // 2. DocDetail 저장
        DocDetail docDetail = (requestDTO instanceof ReceiveDocRequestDTO)
                ? ((ReceiveDocRequestDTO) requestDTO).toDetailEntity(docMaster.getDraftId())
                : ((SendDocRequestDTO) requestDTO).toDetailEntity(docMaster.getDraftId());
        DocDetail lastDocDetail = docDetailRepository
                .findFirstByDocIdNotNullAndDivisionOrderByDocIdDesc(docDetail.getDivision()).orElse(null);
        String docId = (lastDocDetail != null) ? createDocId(lastDocDetail.getDocId()) : createDocId("");

        docDetail.updateDocId(docId);
        docDetailRepository.save(docDetail);

        return docMaster;
    }

    private <T> void getCenterNm(T requestDTO, MultipartFile file, DocMaster docMaster) throws IOException {
        StdGroup centerGroup = stdGroupRepository.findByGroupCd("A001")
                .orElseThrow(() -> new IllegalArgumentException("Not Found"));

        String instCd = (requestDTO instanceof ReceiveDocRequestDTO)
                ? ((ReceiveDocRequestDTO) requestDTO).getInstCd()
                : ((SendDocRequestDTO) requestDTO).getInstCd();

        String division = (requestDTO instanceof ReceiveDocRequestDTO)
                ? ((ReceiveDocRequestDTO) requestDTO).getDivision()
                : ((SendDocRequestDTO) requestDTO).getDivision();

        StdDetail centerDetail = stdDetailRepository.findByGroupCdAndDetailCd(centerGroup, instCd)
                .orElseThrow(() -> new IllegalArgumentException("Not Found"));

        String[] savedFileInfo = handleFileUpload(docMaster.getDrafter(), centerDetail.getDetailNm(), division, file);

        FileUploadRequestDTO fileUploadRequestDTO = FileUploadRequestDTO.builder()
                .draftId(docMaster.getDraftId())
                .fileName(savedFileInfo[0])
                .filePath(savedFileInfo[1])
                .build();

        fileService.uploadFile(fileUploadRequestDTO);
    }

    private String generateDraftId() {
        Optional<DocMaster> lastDocMasterOpt = docMasterRepository.findTopByOrderByDraftIdDesc();

        StdGroup stdGroup = stdGroupRepository.findByGroupCd("A007")
                .orElseThrow(() -> new IllegalArgumentException("Not Found"));
        StdDetail stdDetail = stdDetailRepository.findByGroupCdAndDetailCd(stdGroup, "E")
                .orElseThrow(() -> new IllegalArgumentException("Not Found"));

        if (lastDocMasterOpt.isPresent()) {
            String lastDraftId = lastDocMasterOpt.get().getDraftId();
            int lastIdNum = Integer.parseInt(lastDraftId.substring(2));
            return stdDetail.getEtcItem1() + String.format("%010d", lastIdNum + 1);
        } else {
            return stdDetail.getEtcItem1() + "0000000001";
        }
    }

    private String[] handleFileUpload(String drafter, String centerNm, String division, MultipartFile file) throws IOException {

        String[] fileInfo = new String[2];
        String divisionNm = "A".equals(division) ? "수신" : "발신";

        if (file != null && !file.isEmpty()) {
            String fileName = "(" + centerNm + "_" + divisionNm + ")" + LocalDateTime.now().toLocalDate() + "_" + drafter + "_" + file.getOriginalFilename();
            try {
                String newFileName = sftpClient.uploadFile(file, fileName, docRemoteDirectory);
                String filePath = docRemoteDirectory + "/" + newFileName;
                fileInfo[0] = newFileName;
                fileInfo[1] = filePath;

                return new String[]{newFileName, filePath};
            } catch (Exception e) {
                throw new IOException("SFTP 파일 업로드 실패", e);
            }
        }

        return fileInfo;
    }

    private void grantAdminAuthorityIfAbsent(String firstApproverId, InfoDetailResponseDTO infoDetailResponseDTO) {
        boolean authorityExists = authorityRepository.findAll()
                .stream()
                .anyMatch(authority -> authority.getUserId().equals(firstApproverId));

        if (!authorityExists) {
            AuthorityRequestDTO requestDTO = AuthorityRequestDTO.builder()
                    .userId(firstApproverId)
                    .userNm(infoDetailResponseDTO.getUserName())
                    .userRole("ADMIN")
                    .detailRole(null)
                    .build();

            authorityService.addAdmin(requestDTO);

            stdDetailService.addInfo(
                    StdDetailRequestDTO.builder()
                            .detailCd(firstApproverId)
                            .groupCd("B002")
                            .detailNm(infoDetailResponseDTO.getInstNm() + " " + infoDetailResponseDTO.getDeptNm())
                            .etcItem1(infoDetailResponseDTO.getUserName())
                            .etcItem2("D-2")
                            .build()
            );
        }
    }

    private void updateSidebarPermissionsIfNeeded(String firstApproverId) {
        StdGroup groupB002 = stdGroupRepository.findByGroupCd("B002")
                .orElseThrow(() -> new IllegalArgumentException("Group not found: B002"));
        StdDetail detailB002 = findStdDetail(groupB002, firstApproverId);

        boolean needsUpdate = false;
        List<String> allowedValues = Arrays.asList("D", "D-1", "D-2");

        if (!allowedValues.contains(detailB002.getEtcItem1()) &&
                !allowedValues.contains(detailB002.getEtcItem3()) &&
                !allowedValues.contains(detailB002.getEtcItem5())) {
            detailB002.updateEtcItem3("D-2");
            needsUpdate = true;
        }

        if (stdGroupService.findStdGroupAndCheckFirstApprover("B005", firstApproverId)) {
            needsUpdate = false;
        }

        if (needsUpdate) {
            stdDetailRepository.save(detailB002);
        }
    }

    private StdDetail findStdDetail(StdGroup group, String detailCd) {
        return stdDetailRepository.findByGroupCdAndDetailCd(group, detailCd)
                .orElseThrow(() -> new IllegalArgumentException("Detail not found for group: " + group.getGroupCd()));
    }

    @Override
    @Transactional
    public void updateDocApply(String draftId, DocUpdateRequestDTO docUpdateRequestDTO, MultipartFile file, boolean isFileDeleted) throws IOException {

        // 1. DocMaster 업데이트
        DocMaster docMaster = docMasterRepository.findById(draftId)
                .orElseThrow(() -> new IllegalArgumentException("Not Found"));
        docMaster.setUpdtDt(LocalDateTime.now());
        docMaster.setUpdtrId(docMaster.getDrafterId());
        docMasterRepository.save(docMaster);

        // 2. DocDetail 업데이트
        DocDetail docDetailInfo = docDetailRepository.findById(draftId)
                .orElseThrow(() -> new IllegalArgumentException("Not Found"));

        docHistoryService.createDocHistory(docDetailInfo);

        docDetailInfo.setUpdtDt(LocalDateTime.now());
        docDetailInfo.setUpdtrId(docMaster.getDrafterId());
        updateDocDetail(docUpdateRequestDTO, draftId);

        // 3. File 업데이트
        FileDetail fileDetail = fileDetailRepository.findByDraftId(docMaster.getDraftId()).orElse(null);
        FileHistory fileHistory = null;

        StdGroup centerGroup = stdGroupRepository.findByGroupCd("A001")
                .orElseThrow(() -> new IllegalArgumentException("Not Found"));
        StdDetail centerDetail = stdDetailRepository.findByGroupCdAndDetailCd(centerGroup, docMaster.getInstCd())
                .orElseThrow(() -> new IllegalArgumentException("Not Found"));

        String divisionNm = "A".equals(docDetailInfo.getDivision()) ? "수신" : "발신";

        if (fileDetail != null) {
            fileHistory = fileHistoryRepository.findTopByAttachIdOrderBySeqIdDesc(fileDetail.getAttachId())
                    .orElseThrow(() -> new IllegalArgumentException("Not Found"));
        }

        if (fileDetail != null) {
            fileHistory = fileHistoryRepository.findTopByAttachIdOrderBySeqIdDesc(fileDetail.getAttachId())
                    .orElseThrow(() -> new IllegalArgumentException("Not Found"));
        }

        if (file != null && !file.isEmpty()) {
            if (fileHistory != null && fileHistory.getFilePath() != null) {
                String fileName = "(" + centerDetail.getDetailNm() + "_" + divisionNm + ")" + docMaster.getDraftDate().toLocalDate() + "_" + docMaster.getDrafter() + "_" + file.getOriginalFilename();
                try {
                    sftpClient.deleteFile(fileHistory.getFileName(), docRemoteDirectory);
                    String newFileName = sftpClient.uploadFile(file, fileName, docRemoteDirectory);
                    String newFilePath = docRemoteDirectory + "/" + newFileName;
                    fileService.updateFile(new FileUploadRequestDTO(docMaster.getDraftId(), docMaster.getDrafterId(), newFileName, newFilePath));
                } catch (Exception e) {
                    throw new IOException("SFTP 기존 파일 삭제 실패", e);
                }
            } else {
                String filename = file.getOriginalFilename();
                try {
                    String newFileName = sftpClient.uploadFile(file, filename, docRemoteDirectory);
                    String newFilePath = docRemoteDirectory + "/" + newFileName;
                    fileService.uploadFile(new FileUploadRequestDTO(docMaster.getDraftId(), docMaster.getDrafterId(), newFileName, newFilePath));
                } catch (Exception e) {
                    throw new IOException("SFTP 파일 업로드 실패", e);
                }
            }
        }
        else if (isFileDeleted && fileHistory != null && fileHistory.getFilePath() != null) {
            try {
                sftpClient.deleteFile(fileHistory.getFilePath(), docRemoteDirectory);
                fileDetail.updateUseAt("N");
            } catch (Exception e) {
                throw new IOException("SFTP 파일 삭제 실패", e);
            }
        }
    }

    @Override
    @Transactional
    public void cancelDocApply(String draftId) {

        // 1. DocMaster 삭제 (F)
        DocMaster docMaster = docMasterRepository.findById(draftId)
                .orElseThrow(() -> new IllegalArgumentException("Not Found"));
        docMaster.updateStatus("F");
        docMasterRepository.save(docMaster);

        // 2. FileDetail History 업데이트
        FileDetail fileDetail = fileDetailRepository.findByDraftId(draftId)
                .orElseThrow(() -> new IllegalArgumentException("Not Found"));
        fileDetail.updateUseAt("N");
    }

    @Override
    @Transactional(readOnly = true)
    public DocDetailResponseDTO getDoc(String draftId) {
        DocMaster docMaster = docMasterRepository.findById(draftId)
                .orElseThrow(() -> new IllegalArgumentException("Not Found"));

        DocDetail docDetail = docDetailRepository.findById(draftId)
                .orElseThrow(() -> new IllegalArgumentException("Not Found"));

        FileDetail fileDetail = fileDetailRepository.findByDraftId(docMaster.getDraftId())
                .orElse(null);
        FileHistory fileHistory = null;
        if (fileDetail != null) {
            fileHistory = fileHistoryRepository.findTopByAttachIdOrderBySeqIdDesc(fileDetail.getAttachId())
                    .orElseThrow(() -> new IllegalArgumentException("Not Found"));
        }

        return DocDetailResponseDTO.of(docMaster, docDetail, fileHistory);
    }

    @Override
    @Transactional(readOnly = true)
    public List<DocMyResponseDTO> getMyDocApply(LocalDateTime startDate, LocalDateTime endDate, String userId) {
        return new ArrayList<>(this.getMyDocMasterList(startDate, endDate, userId));
    }

    @Override
    public Page<DocMyResponseDTO> getMyDocApply2(ApplyRequestDTO applyRequestDTO, PostSearchRequestDTO postSearchRequestDTO, Pageable page) {
        return docApplyQueryRepository.getMyDocApply2(applyRequestDTO, postSearchRequestDTO, page);
    }

    @Override
    public List<DocMyResponseDTO> getMyDocApply(ApplyRequestDTO applyRequestDTO, PostSearchRequestDTO postSearchRequestDTO) {
        return docApplyQueryRepository.getMyDocMasterList2(applyRequestDTO, postSearchRequestDTO);
    }

    public List<DocMyResponseDTO> getMyDocMasterList(LocalDateTime startDate, LocalDateTime endDate, String userId) {
        List<DocMaster> docMasterList = docMasterRepository.findByDrafterIdAndDraftDateBetween(userId, startDate, endDate)
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
    public List<DocPendingResponseDTO> getMyDocPendingList(ApplyRequestDTO applyRequestDTO) {
        return new ArrayList<>(this.getMyDocPendingMasterList(applyRequestDTO.getUserId()));
    }

    @Override
    public Page<DocMasterResponseDTO> getDocApply2(ApplyRequestDTO applyRequestDTO, PostSearchRequestDTO postSearchRequestDTO, Pageable page) {
        return docApplyQueryRepository.getDocApply2(applyRequestDTO, postSearchRequestDTO, page);
    }

    @Override
    @Transactional(readOnly = true)
    public List<DocMasterResponseDTO> getDocApply(ApplyRequestDTO applyRequestDTO, PostSearchRequestDTO postSearchRequestDTO) {

        LocalDateTime startDate = convertStringToLocalDateTime(postSearchRequestDTO.getStartDate());
        LocalDateTime endDate = convertStringToLocalDateTime(postSearchRequestDTO.getEndDate());

        String searchType = postSearchRequestDTO.getSearchType();
        String keyword = postSearchRequestDTO.getKeyword();

        List<DocMaster> docMasters = docMasterRepository.findAllByStatusNotAndInstCdAndDraftDateBetweenOrderByDraftDateDesc("F", applyRequestDTO.getInstCd(), startDate, endDate);

        if (docMasters == null) {
            docMasters = new ArrayList<>();
        }

        return docMasters.stream()
                .filter(docMaster -> SearchUtils.matchesSearchCriteria(searchType,keyword, docMaster.getTitle(), docMaster.getDrafter()))
                .map(docMaster -> {
                    DocDetail docDetail = docDetailRepository.findById(docMaster.getDraftId())
                            .orElseThrow(() -> new IllegalArgumentException("Division Not Found"));
                    DocMasterResponseDTO docMasterResponseDTO = DocMasterResponseDTO.of(docMaster, docDetail.getDivision());
                    docMasterResponseDTO.setInstNm(stdBcdService.getInstNm(docMaster.getInstCd()));
                    return docMasterResponseDTO;
                })
                .toList();
    }

    public LocalDateTime convertStringToLocalDateTime(String dateString) {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        try {
            LocalDate date = LocalDate.parse(dateString, formatter);
            return date.atStartOfDay();
        } catch (DateTimeParseException e) {
            System.out.println("Date parsing error: " + e.getMessage());
            return null;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<DocPendingResponseDTO> getDocPendingList(LocalDateTime startDate, LocalDateTime endDate, String instCd, String userId) {
        List<DocMaster> docMasters = docMasterRepository
                .findAllByStatusAndInstCdAndDraftDateBetweenOrderByDraftDateDesc("A", instCd, startDate, endDate);

        return docMasters.stream()
                .filter(docMaster -> {
                    String[] approverChainArray = docMaster.getApproverChain().split(", ");
                    int currentIndex = docMaster.getCurrentApproverIndex();

                    return currentIndex < approverChainArray.length && approverChainArray[currentIndex].equals(userId);
                })
                .map(docMaster -> {
                    DocDetail docDetail = docDetailRepository.findById(docMaster.getDraftId())
                            .orElseThrow(() -> new IllegalArgumentException("Division Not Found"));

                    DocPendingResponseDTO docPendingResponseDTO = DocPendingResponseDTO.of(docMaster, docDetail.getDivision(), null);
                    docPendingResponseDTO.setInstNm(stdBcdService.getInstNm(docMaster.getInstCd()));

                    return docPendingResponseDTO;
                }).toList();
    }

    @Override
    public Page<DocPendingResponseDTO> getDocPendingList2(ApplyRequestDTO applyRequestDTO, PostSearchRequestDTO postSearchRequestDTO, Pageable page) {
        return docPendingQueryRepository.getDocPending2(applyRequestDTO, postSearchRequestDTO, page);
    }

    public List<DocPendingResponseDTO> getMyDocPendingMasterList(String userId) {
        List<DocMaster> docMasterList = docMasterRepository.findByDrafterIdAndStatusAndCurrentApproverIndex(userId, "A", 0)
                .orElseThrow(() -> new IllegalArgumentException("Not Found"));

        return docMasterList.stream()
                .map(docMaster -> {
                    DocDetail docDetail = docDetailRepository.findById(docMaster.getDraftId())
                            .orElseThrow(() -> new IllegalArgumentException("Division Not Found"));
                    String updaterId = docMaster.getUpdtrId();
                    String updaterNm = updaterId != null ? infoService.getUserInfoDetail(updaterId).getUserName() : null;
                    return DocPendingResponseDTO.of(docMaster, docDetail.getDivision(), updaterNm);
                }).toList();
    }

    private void updateDocDetail(Object docRequestOrUpdateDTO, String draftId) {
        DocDetail existingDocDetail = docDetailRepository.findById(draftId)
                .orElseThrow(() -> new IllegalArgumentException("Not Found"));

        if (docRequestOrUpdateDTO instanceof SendDocRequestDTO sendDocRequestDTO) {
            existingDocDetail.update(sendDocRequestDTO);
        } else if (docRequestOrUpdateDTO instanceof DocUpdateRequestDTO docUpdateRequestDTO) {
            existingDocDetail.updateFile(docUpdateRequestDTO);
        }

        docDetailRepository.save(existingDocDetail);
    }
}
