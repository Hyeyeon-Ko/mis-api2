package kr.or.kmi.mis.api.file.model.request;

import lombok.Builder;
import lombok.Data;

@Data
public class FileUploadRequestDTO {

    private String draftId;
    private String fileName;
    private String filePath;

    @Builder
    public FileUploadRequestDTO(String draftId, String fileName, String filePath) {
        this.draftId = draftId;
        this.fileName = fileName;
        this.filePath = filePath;
    }
}
