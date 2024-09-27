package kr.or.kmi.mis.api.file.model.entity;

import jakarta.persistence.*;
import kr.or.kmi.mis.cmm.model.entity.BaseSystemFieldEntity;
import lombok.*;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
@Table(name = "hrmtfdlh")
public class FileDownloadHistory extends BaseSystemFieldEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "draft_id", nullable = false)
    private Long draftId;

    @Column(name = "doc_type", nullable = false)
    private String docType;

    @Column(name = "file_name", nullable = false)
    private String fileName;

    @Column(name = "file_path", nullable = false)
    private String filePath;

    @Column(name = "reason", nullable = false)
    private String reason;

    @Column(name = "downloader_name", nullable = false)
    private String downloaderNm;

    @Column(name = "downloader_id", nullable = false)
    private String downloaderId;

    @Builder
    public FileDownloadHistory(Long draftId, String docType, String fileName, String filePath, String reason, String downloaderNm, String downloaderId) {
        this.draftId = draftId;
        this.docType = docType;
        this.fileName = fileName;
        this.filePath = filePath;
        this.reason = reason;
        this.downloaderNm = downloaderNm;
        this.downloaderId = downloaderId;
    }
}
