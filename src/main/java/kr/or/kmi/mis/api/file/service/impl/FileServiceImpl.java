package kr.or.kmi.mis.api.file.service.impl;

import kr.or.kmi.mis.api.file.model.entity.FileDetail;
import kr.or.kmi.mis.api.file.model.entity.FileHistory;
import kr.or.kmi.mis.api.file.model.request.FileUploadRequestDTO;
import kr.or.kmi.mis.api.file.repository.FileDetailRepository;
import kr.or.kmi.mis.api.file.repository.FileHistoryRepository;
import kr.or.kmi.mis.api.file.service.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.time.LocalDateTime;

@Service
@Transactional
@RequiredArgsConstructor
public class FileServiceImpl implements FileService {

    private final FileDetailRepository fileDetailRepository;
    private final FileHistoryRepository fileHistoryRepository;

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

        if (lastFileDetailOpt.isPresent()) {
            String lastAttachId = lastFileDetailOpt.get().getAttachId();
            int lastIdNum = Integer.parseInt(lastAttachId.substring(2));
            return "at" + String.format("%010d", lastIdNum + 1);
        } else {
            // TODO: draftId 관련 기준자료 추가 후 수정!!!
            return "at0000000001";
        }
    }

    private Long generateSeqId(String attachId) {
        Optional<FileHistory> fileHistoryOpt = fileHistoryRepository.findTopByAttachIdOrderBySeqIdDesc(attachId);

        Long maxSeqId = fileHistoryOpt.map(FileHistory::getSeqId).orElse(0L);

        return maxSeqId + 1;
    }
}
