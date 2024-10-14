package kr.or.kmi.mis.api.file.service.impl;

import kr.or.kmi.mis.api.file.model.entity.FileDetail;
import kr.or.kmi.mis.api.file.model.entity.FileHistory;
import kr.or.kmi.mis.api.file.model.request.FileUploadRequestDTO;
import kr.or.kmi.mis.api.file.repository.FileDetailRepository;
import kr.or.kmi.mis.api.file.repository.FileHistoryRepository;
import kr.or.kmi.mis.api.file.service.FileService;
import kr.or.kmi.mis.api.std.model.entity.StdDetail;
import kr.or.kmi.mis.api.std.model.entity.StdGroup;
import kr.or.kmi.mis.api.std.repository.StdDetailRepository;
import kr.or.kmi.mis.api.std.repository.StdGroupRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class FileServiceImpl implements FileService {

    private final FileDetailRepository fileDetailRepository;
    private final FileHistoryRepository fileHistoryRepository;
    private final StdGroupRepository stdGroupRepository;
    private final StdDetailRepository stdDetailRepository;

    @Override
    public void uploadFile(FileUploadRequestDTO fileUploadRequestDTO) {
        String attachId = generateAttachId();
        Long seqId = generateSeqId(attachId);

        // 1. FileDetail 업로드
        FileDetail fileDetail = new FileDetail(fileUploadRequestDTO, attachId);
        fileDetail.setRgstDt(LocalDateTime.now());
        fileDetailRepository.save(fileDetail);

        // 2. FileHistory 업로드
        FileHistory fileHistory = new FileHistory(fileUploadRequestDTO, attachId, seqId);
        fileHistory.setRgstDt(LocalDateTime.now());
        fileHistoryRepository.save(fileHistory);
    }

    @Override
    public void updateFile(FileUploadRequestDTO fileUploadRequestDTO) {

        FileDetail fileDetail = fileDetailRepository.findByDraftId(fileUploadRequestDTO.getDraftId())
                .orElseThrow(() -> new IllegalArgumentException("Not Found"));

        Long seqId = generateSeqId(fileDetail.getAttachId());
        FileHistory fileHistory = new FileHistory(fileUploadRequestDTO, fileDetail.getAttachId(), seqId);
        fileHistory.setRgstDt(LocalDateTime.now());
        fileHistoryRepository.save(fileHistory);
    }

    private String generateAttachId() {
        Optional<FileDetail> lastFileDetailOpt = fileDetailRepository.findTopByOrderByAttachIdDesc();

        StdGroup stdGroup = stdGroupRepository.findByGroupCd("A007")
                .orElseThrow(() -> new IllegalArgumentException("Not Found"));
        StdDetail stdDetail = stdDetailRepository.findByGroupCdAndDetailCd(stdGroup, "F")
                .orElseThrow(() -> new IllegalArgumentException("Not Found"));

        if (lastFileDetailOpt.isPresent()) {
            String lastAttachId = lastFileDetailOpt.get().getAttachId();
            int lastIdNum = Integer.parseInt(lastAttachId.substring(2));
            return stdDetail.getEtcItem1() + String.format("%010d", lastIdNum + 1);
        } else {
            return stdDetail.getEtcItem1() + "0000000001";
        }
    }

    private Long generateSeqId(String attachId) {
        Optional<FileHistory> fileHistoryOpt = fileHistoryRepository.findTopByAttachIdOrderBySeqIdDesc(attachId);

        Long maxSeqId = fileHistoryOpt.map(FileHistory::getSeqId).orElse(0L);

        return maxSeqId + 1;
    }
}
