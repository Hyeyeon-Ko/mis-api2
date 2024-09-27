package kr.or.kmi.mis.api.file.model.entity;

import jakarta.persistence.*;
import kr.or.kmi.mis.api.bcd.model.entity.DraftSeqPK;
import kr.or.kmi.mis.cmm.model.entity.BaseSystemFieldEntity;
import lombok.*;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
@Table(name = "hrmtfiled_hist")
@IdClass(DraftSeqPK.class)
public class FileHistory extends BaseSystemFieldEntity {

    @Id
    @Column(name = "draft_id")
    private Long draftId;

    @Id
    @Column(name = "seq_id")
    private Long seqId;

    @Column(name = "doc_type")
    private String docType;

    @Column(name = "file_name", nullable = false)
    private String fileName;

    @Column(name = "file_path", nullable = false)
    private String filePath;

    @Column(name = "type", nullable = false)
    private String type; // A: 수정, B: 삭제

    @Builder
    public FileHistory(FileDetail fileDetail, Long seqId, String type) {
        this.draftId = fileDetail.getDraftId();
        this.seqId = seqId;
        this.docType = fileDetail.getDocType();
        this.fileName = fileDetail.getFileName();
        this.filePath = fileDetail.getFilePath();
        this.type = type;
    }
}
