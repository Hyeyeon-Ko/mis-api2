package kr.or.kmi.mis.api.doc.model.response;

import kr.or.kmi.mis.api.doc.model.entity.DocDetail;
import kr.or.kmi.mis.api.doc.model.entity.DocMaster;
import kr.or.kmi.mis.api.file.model.entity.FileDetail;
import kr.or.kmi.mis.api.file.model.entity.FileHistory;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@Builder
@Data
@AllArgsConstructor
public class DocDetailResponseDTO {

    private String draftId;
    private LocalDateTime draftDate;
    private LocalDateTime lastUpdateDate;
    private String drafter;
    private String division;
    private String receiver;
    private String sender;
    private String docTitle;
    private String purpose;
    private String fileName;
    private String filePath;

    // 파일 다운로드 URL
    public String getFileUrl() {
        return fileName != null ? "/api/doc/download/" + fileName : null;
    }

    public static DocDetailResponseDTO of(DocMaster docMaster, DocDetail docDetail, FileHistory fileHistory) {
        return DocDetailResponseDTO.builder()
                .draftId(docMaster.getDraftId())
                .draftDate(docMaster.getDraftDate())
                .lastUpdateDate(docDetail.getUpdtDt())
                .drafter(docMaster.getDrafter())
                .division(docDetail.getDivision())
                .receiver(docDetail.getReceiver())
                .sender(docDetail.getSender())
                .docTitle(docDetail.getDocTitle())
                .purpose(docDetail.getPurpose())
                .fileName(fileHistory != null ? fileHistory.getFileName() : "")
                .filePath(fileHistory != null ? fileHistory.getFilePath() : "")
                .build();
    }
}
