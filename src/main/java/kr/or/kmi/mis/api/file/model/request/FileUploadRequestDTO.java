package kr.or.kmi.mis.api.file.model.request;

import lombok.Builder;
import lombok.Data;

@Data
public class FileUploadRequestDTO {

    private Long draftId;
    private String drafter;
    private String docType;
    private String fileName;
    private String filePath;

    @Builder
    public FileUploadRequestDTO(Long draftId, String drafter, String docType, String fileName, String filePath) {
        this.draftId = draftId;
        this.drafter = drafter;
        this.docType = docType;
        this.fileName = fileName;
        this.filePath = filePath;
    }
}
