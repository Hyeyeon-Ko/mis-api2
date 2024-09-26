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
    private final StdBcdService stdBcdService;
    private final InfoService infoService;
    private final SftpClient sftpClient;

    @Value("${sftp.remote-directory.corpdoc}")
    private String corpdocRemoteDirectory;

    @Override
    @Transactional
    public void createCorpDocApply(CorpDocRequestDTO corpDocRequestDTO, MultipartFile file) throws Exception {
        CorpDocMaster corpDocMaster = corpDocRequestDTO.toMasterEntity();
        corpDocMaster.setRgstrId(corpDocRequestDTO.getDrafterId());
        corpDocMaster.setRgstDt(new Timestamp(System.currentTimeMillis()));
        corpDocMaster = corpDocMasterRepository.save(corpDocMaster);

        String[] savedFileInfo = handleFileUpload(file, null);

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
                                   MultipartFile file, boolean isFileDeleted) throws Exception {
        CorpDocDetail corpDocDetail = corpDocDetailRepository.findById(draftId)
                .orElseThrow(() -> new IllegalArgumentException("Not Found"));

        corpDocHistoryService.createCorpDocHistory(corpDocDetail);

        String[] savedFileInfo = handleFileUpload(file, corpDocDetail.getFilePath(), isFileDeleted);

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

    private String[] handleFileUpload(MultipartFile file, String existingFilePath) throws Exception {
        return handleFileUpload(file, existingFilePath, false);
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
