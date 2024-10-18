package kr.or.kmi.mis.api.file.model.entity;

import jakarta.persistence.*;
import kr.or.kmi.mis.api.file.model.request.FileUploadRequestDTO;
import kr.or.kmi.mis.cmm.model.entity.BaseSystemFieldEntity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
@Table(name = "hrmtfiled_hist")
@IdClass(IdSeqPK.class)
public class FileHistory extends BaseSystemFieldEntity {

    @Id
    @Column(name = "attach_id", length = 12)
    private String attachId;

    @Id
    @Column(name = "seq_id")
    private Long seqId;

    @Column(name = "file_nm", nullable = false)
    private String fileName;

    @Column(name = "file_path", nullable = false)
    private String filePath;

    @Builder
    public FileHistory(Long seqId) {
        this.seqId = seqId;
    }

    public FileHistory(FileUploadRequestDTO fileUploadRequestDTO, String attachId, Long seqId) {
        this.attachId = attachId;
        this.seqId = seqId;
        this.fileName = fileUploadRequestDTO.getFileName();
        this.filePath = fileUploadRequestDTO.getFilePath();
    }

    public void update(FileUploadRequestDTO fileUploadRequestDTO, Long seqId) {
        this.seqId = seqId;
        this.fileName = fileUploadRequestDTO.getFileName();
        this.filePath = fileUploadRequestDTO.getFilePath();
    }
}
