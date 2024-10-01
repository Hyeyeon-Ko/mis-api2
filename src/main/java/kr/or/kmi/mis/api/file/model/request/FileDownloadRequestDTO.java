package kr.or.kmi.mis.api.file.model.request;

import kr.or.kmi.mis.api.file.model.entity.FileDownloadHistory;
import lombok.Data;

@Data
public class FileDownloadRequestDTO {

    private String draftId;
    private String downloadType;
    private String downloadNotes;
    private String downloaderNm;
    private String downloaderId;

    public FileDownloadHistory toEntity(String fileName, FileDownloadRequestDTO fileDownloadRequestDTO) {
        return FileDownloadHistory.builder()
                .draftId(fileDownloadRequestDTO.getDraftId())
                .downloadType(fileDownloadRequestDTO.getDownloadType())
                .downloadNotes(fileDownloadRequestDTO.getDownloadNotes())
                .downloaderNm(fileDownloadRequestDTO.getDownloaderNm())
                .downloaderId(fileDownloadRequestDTO.getDownloaderId())
                .build();
    }
}
