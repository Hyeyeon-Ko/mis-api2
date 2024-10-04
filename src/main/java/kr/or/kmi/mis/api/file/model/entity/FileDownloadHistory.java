package kr.or.kmi.mis.api.file.model.entity;

import jakarta.persistence.*;
import kr.or.kmi.mis.cmm.model.entity.BaseSystemFieldEntity;
import lombok.*;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
@Table(name = "hrmtfldl_hist")
public class FileDownloadHistory extends BaseSystemFieldEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "sn")
    private Long sn;

    @Column(name = "attach_id", length = 12, nullable = false)
    private String attachId;

    @Column(name = "draft_id", length = 12, nullable = false)
    private String draftId;

    @Column(name = "download_type", length = 1, nullable = false)
    private String downloadType;

    @Column(name = "download_notes", length = 50)
    private String downloadNotes;

    @Column(name = "downloader_nm", length = 20, nullable = false)
    private String downloaderNm;

    @Column(name = "downloader_id", length = 20, nullable = false)
    private String downloaderId;

    @Builder
    public FileDownloadHistory(String attachId, String draftId,
                               String downloadType, String downloadNotes,
                               String downloaderNm, String downloaderId) {
        this.attachId = attachId;
        this.draftId = draftId;
        this.downloadType = downloadType;
        this.downloadNotes = downloadNotes;
        this.downloaderNm = downloaderNm;
        this.downloaderId = downloaderId;
    }
}
