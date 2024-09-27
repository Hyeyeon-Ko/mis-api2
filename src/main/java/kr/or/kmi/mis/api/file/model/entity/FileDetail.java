package kr.or.kmi.mis.api.file.model.entity;

import jakarta.persistence.*;
import kr.or.kmi.mis.api.file.model.request.FileUploadRequestDTO;
import kr.or.kmi.mis.cmm.model.entity.BaseSystemFieldEntity;
import lombok.*;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
@Table(name = "hrmtfiled")
public class FileDetail extends BaseSystemFieldEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "draft_id")
    private Long draftId;

    @Column(name = "doc_type", nullable = false)
    private String docType; // A: 인장(반출), B: 법인서류, C: 문서수발신

    @Column(name = "file_name", nullable = false)
    private String fileName;

    @Column(name = "file_path", nullable = false)
    private String filePath;

    @Builder
    public FileDetail(String docType, String fileName, String filePath) {
        this.docType = docType;
        this.fileName = fileName;
        this.filePath = filePath;
    }

    public FileDetail(FileUploadRequestDTO fileUploadRequestDTO) {
        this.draftId = fileUploadRequestDTO.getDraftId();
        this.docType = fileUploadRequestDTO.getDocType();
        this.fileName = fileUploadRequestDTO.getFileName();
        this.filePath = fileUploadRequestDTO.getFilePath();
    }

    public void updateFileInfo(String fileName, String filePath) {
        this.fileName = fileName;
        this.filePath = filePath;
    }
}
