package kr.or.kmi.mis.api.doc.service.impl;

import kr.or.kmi.mis.api.apply.model.request.ApplyRequestDTO;
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

import java.io.IOException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
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

        StdGroup stdGroup = stdGroupRepository.findByGroupCd("C002")
                .orElseThrow(() -> new IllegalArgumentException("Not Found"));
        List<StdDetail> stdDetail = stdDetailRepository.findByGroupCdAndEtcItem1(stdGroup, receiveDocRequestDTO.getInstCd())
                .orElseThrow(() -> new IllegalArgumentException("Not Found"));

        docMaster.updateApproverChain(stdDetail.getFirst().getEtcItem3());
        docMaster = docMasterRepository.save(docMaster);

        // 2. DocDetail 저장
        DocDetail docDetail = receiveDocRequestDTO.toDetailEntity(docMaster.getDraftId());
        docDetailRepository.save(docDetail);

        // 3. File 업로드
        String[] savedFileInfo = handleFileUpload(file);

        FileUploadRequestDTO fileUploadRequestDTO = FileUploadRequestDTO.builder()
                .draftId(draftId)
                .fileName(savedFileInfo[0])
                .filePath(savedFileInfo[1])
                .build();

        fileService.uploadFile(fileUploadRequestDTO);
    }

    @Override
    @Transactional
    public void applyReceiveDocByLeader(ReceiveDocRequestDTO receiveDocRequestDTO, MultipartFile file) throws IOException {
        String draftId = generateDraftId();

        // 1. DocMaster 저장
        DocMaster docMaster = receiveDocRequestDTO.toMasterEntity(draftId, "E");
        docMaster.updateRespondDate(LocalDateTime.now());
        docMaster = docMasterRepository.save(docMaster);

        // 2. DocDetail 저장
        DocDetail docDetail = receiveDocRequestDTO.toDetailEntity(docMaster.getDraftId());
        DocDetail lastDocDetail = docDetailRepository
                .findFirstByDocIdNotNullAndDivisionOrderByDocIdDesc(docDetail.getDivision()).orElse(null);
        String docId = (lastDocDetail != null) ? createDocId(lastDocDetail.getDocId()) : createDocId("");

        docDetail.updateDocId(docId);
        docDetailRepository.save(docDetail);

        // 3. File 업로드
        String[] savedFileInfo = handleFileUpload(file);

        FileUploadRequestDTO fileUploadRequestDTO = FileUploadRequestDTO.builder()
                .draftId(draftId)
                .fileName(savedFileInfo[0])
                .filePath(savedFileInfo[1])
                .build();

        fileService.uploadFile(fileUploadRequestDTO);
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
        String[] savedFileInfo = handleFileUpload(file);

        FileUploadRequestDTO fileUploadRequestDTO = FileUploadRequestDTO.builder()
                .draftId(draftId)
                .fileName(savedFileInfo[0])
                .filePath(savedFileInfo[1])
                .build();

        fileService.uploadFile(fileUploadRequestDTO);
    }

    @Override
    @Transactional
    public void applySendDocByLeader(SendDocRequestDTO sendDocRequestDTO, MultipartFile file) throws IOException {
        String draftId = generateDraftId();

        // 1. DocMaster 저장
        DocMaster docMaster = sendDocRequestDTO.toMasterEntity(draftId,"E");
        docMaster.updateRespondDate(LocalDateTime.now());
        docMaster = docMasterRepository.save(docMaster);

        // 2. DocDetail 저장
        DocDetail docDetail = sendDocRequestDTO.toDetailEntity(docMaster.getDraftId());
        DocDetail lastDocDetail = docDetailRepository
                .findFirstByDocIdNotNullAndDivisionOrderByDocIdDesc(docDetail.getDivision()).orElse(null);
        String docId = (lastDocDetail != null) ? createDocId(lastDocDetail.getDocId()) : createDocId("");

        docDetail.updateDocId(docId);
        docDetailRepository.save(docDetail);

        // 3. File 업로드
        String[] savedFileInfo = handleFileUpload(file);

        FileUploadRequestDTO fileUploadRequestDTO = FileUploadRequestDTO.builder()
                .draftId(draftId)
                .fileName(savedFileInfo[0])
                .filePath(savedFileInfo[1])
                .build();

        fileService.uploadFile(fileUploadRequestDTO);
    }

    // TODO 수정하다 멈춤
    private String generateDraftId() {
        Optional<DocMaster> lastDocMasterOpt = docMasterRepository.findTopByOrderByDraftIdDesc();

        StdGroup stdGroup = stdGroupRepository.findByGroupCd("A007")
                .orElseThrow(() -> new IllegalArgumentException("Not Found"));

        if (lastDocMasterOpt.isPresent()) {
            String lastDraftId = lastDocMasterOpt.get().getDraftId();
            int lastIdNum = Integer.parseInt(lastDraftId.substring(2));
            return "rs" + String.format("%010d", lastIdNum + 1);
        } else {
            return "0000000001";
        }
    }

    private String[] handleFileUpload(MultipartFile file) throws IOException {

        String[] fileInfo = new String[2];

        if (file != null && !file.isEmpty()) {
            String fileName = file.getOriginalFilename();
            try {
                sftpClient.uploadFile(file, fileName, docRemoteDirectory);
                String filePath = docRemoteDirectory + "/" + fileName;
                fileInfo[0] = fileName;
                fileInfo[1] = filePath;
            } catch (Exception e) {
                throw new IOException("SFTP 파일 업로드 실패", e);
            }
        }

        return fileInfo;
    }

    private void grantAdminAuthorityIfAbsent(String firstApproverId, InfoDetailResponseDTO infoDetailResponseDTO) {
        boolean authorityExists = authorityRepository.findAllByDeletedtIsNull()
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
        StdGroup groupB002 = findStdGroup("B002");
        StdDetail detailB002 = findStdDetail(groupB002, firstApproverId);

        boolean needsUpdate = false;
        List<String> allowedValues = Arrays.asList("D", "D-1", "D-2");

        if (!allowedValues.contains(detailB002.getEtcItem1()) && !allowedValues.contains(detailB002.getEtcItem3()) && !allowedValues.contains(detailB002.getEtcItem5())) {
            detailB002.updateEtcItem3("D-2");
            needsUpdate = true;
        }

        StdGroup stdGroupC002 = findStdGroup("C002");
        List<StdDetail> detailsC002 = findAllActiveDetails(stdGroupC002);

        if (detailsC002.stream().anyMatch(detail -> firstApproverId.equals(detail.getEtcItem2()) || firstApproverId.equals(detail.getEtcItem3()))) {
            needsUpdate = false;
        }

        if (needsUpdate) {
            stdDetailRepository.save(detailB002);
        }
    }

    private StdGroup findStdGroup(String groupCd) {
        return stdGroupRepository.findByGroupCd(groupCd)
                .orElseThrow(() -> new IllegalArgumentException("Group not found: " + groupCd));
    }

    private StdDetail findStdDetail(StdGroup group, String detailCd) {
        return stdDetailRepository.findByGroupCdAndDetailCd(group, detailCd)
                .orElseThrow(() -> new IllegalArgumentException("Detail not found for group: " + group.getGroupCd()));
    }

    private List<StdDetail> findAllActiveDetails(StdGroup group) {
        return stdDetailRepository.findAllByUseAtAndGroupCd("Y", group)
                .orElseThrow(() -> new IllegalArgumentException("No active details found for group: " + group.getGroupCd()));
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
                String newFileName = file.getOriginalFilename();
                try {
                    String newFilePath = docRemoteDirectory + "/" + newFileName;
                    sftpClient.deleteFile(fileHistory.getFileName(), docRemoteDirectory);
                    sftpClient.uploadFile(file, newFileName, docRemoteDirectory);
                    fileService.updateFile(new FileUploadRequestDTO(docMaster.getDraftId(), newFileName, newFilePath));
                } catch (Exception e) {
                    throw new IOException("SFTP 기존 파일 삭제 실패", e);
                }
            } else {
                String newFileName = file.getOriginalFilename();
                try {
                    String newFilePath = docRemoteDirectory + "/" + newFileName;
                    sftpClient.uploadFile(file, newFileName, docRemoteDirectory);
                    fileService.uploadFile(new FileUploadRequestDTO(docMaster.getDraftId(), newFileName, newFilePath));
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
    public List<DocPendingResponseDTO> getMyDocPendingList(String userId) {
        return new ArrayList<>(this.getMyDocPendingMasterList(userId));
    }

    @Override
    public Page<DocMasterResponseDTO> getDocApply2(ApplyRequestDTO applyRequestDTO, PostSearchRequestDTO postSearchRequestDTO, Pageable page) {
        return docApplyQueryRepository.getDocApply2(applyRequestDTO, postSearchRequestDTO, page);
    }

    @Override
    @Transactional(readOnly = true)
    public List<DocMasterResponseDTO> getDocApply(LocalDateTime startDate, LocalDateTime endDate, String searchType, String keyword, String instCd, String userId) {
        List<DocMaster> docMasters = docMasterRepository.findAllByStatusNotAndInstCdAndDraftDateBetweenOrderByDraftDateDesc("F", instCd, startDate, endDate);

        if (docMasters == null) {
            docMasters = new ArrayList<>();
        }

        return docMasters.stream()
                .filter(docMaster -> {
                    if (docMaster.getStatus().equals("A")) {
                        String[] approverChainArray = docMaster.getApproverChain().split(", ");
                        int currentIndex = docMaster.getCurrentApproverIndex();

                        if (currentIndex >= approverChainArray.length || !approverChainArray[currentIndex].equals(userId)) {
                            return false;
                        }
                    }

                    if (searchType != null && keyword != null) {
                        return switch (searchType) {
                            case "전체" ->
                                    docMaster.getTitle().contains(keyword) || docMaster.getDrafter().contains(keyword);
                            case "제목" -> docMaster.getTitle().contains(keyword);
                            case "신청자" -> docMaster.getDrafter().contains(keyword);
                            default -> true;
                        };
                    }
                    return true;
                })
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

                    DocPendingResponseDTO docPendingResponseDTO = DocPendingResponseDTO.of(docMaster, docDetail.getDivision());
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
                    return DocPendingResponseDTO.of(docMaster, docDetail.getDivision());
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
