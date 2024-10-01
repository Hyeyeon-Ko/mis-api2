//package kr.or.kmi.mis.api.file.service.impl;
//
//import kr.or.kmi.mis.api.file.model.entity.FileDetail;
//import kr.or.kmi.mis.api.file.model.entity.FileHistory;
//import kr.or.kmi.mis.api.file.repository.FileHistoryRepository;
//import kr.or.kmi.mis.api.file.service.FileHistorySevice;
//import kr.or.kmi.mis.api.user.service.InfoService;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.sql.Timestamp;
//
//@Service
//@RequiredArgsConstructor
//public class FileHistoryServiceImpl implements FileHistorySevice {
//
//    private final FileHistoryRepository fileHistoryRepository;
//    private final InfoService infoService;
//
//    @Override
//    @Transactional
//    public void createFileHistory(FileDetail fileDetail, String type) {
//
//        Long maxSeqId = fileHistoryRepository.findTopByAttachId(fileDetail.getAttachId())
//                .map(FileHistory::getSeqId).orElse(0L);
//
//        FileHistory fileHistory = FileHistory.builder()
//                .seqId(maxSeqId+1)
//                .build();
//
//        fileHistory.setRgstrId(fileDetail.getRgstrId());
//        fileHistory.setRgstDt(fileDetail.getRgstDt());
//        fileHistory.setUpdtrId(infoService.getUserInfo().getUserId());
//        fileHistory.setUpdtDt(new Timestamp(System.currentTimeMillis()));
//
//        fileHistoryRepository.save(fileHistory);
//    }
//}
