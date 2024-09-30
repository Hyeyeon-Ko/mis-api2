package kr.or.kmi.mis.api.file.model.request;

import kr.or.kmi.mis.api.file.model.entity.FileDownloadHistory;
import lombok.Data;

@Data
public class FileDownloadRequestDTO {

    private Long draftId;
    private String docType;
    private String fileType;
    private String reason;
    private String downloaderNm;
    private String downloaderId;

    public FileDownloadHistory toEntity(String fileName, FileDownloadRequestDTO fileDownloadRequestDTO) {
        return FileDownloadHistory.builder()
                .draftId(fileDownloadRequestDTO.getDraftId())
                .docType(fileDownloadRequestDTO.getDocType())
                .fileName(fileName)
                .fileType(fileDownloadRequestDTO.getFileType())
                .reason(fileDownloadRequestDTO.getReason())
                .downloaderNm(fileDownloadRequestDTO.getDownloaderNm())
                .downloaderId(fileDownloadRequestDTO.getDownloaderId())
                .build();
    }
}
