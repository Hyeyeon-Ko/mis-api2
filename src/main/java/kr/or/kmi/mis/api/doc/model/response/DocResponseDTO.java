package kr.or.kmi.mis.api.doc.model.response;

import kr.or.kmi.mis.api.doc.model.entity.DocDetail;
import kr.or.kmi.mis.api.doc.model.entity.DocMaster;
import kr.or.kmi.mis.api.file.model.entity.FileDetail;
import kr.or.kmi.mis.api.file.model.entity.FileHistory;
import lombok.Builder;
import lombok.Data;

import java.text.SimpleDateFormat;

@Builder
@Data
public class DocResponseDTO {

    private String draftId;
    private String draftDate;
    private String drafter;
    private String docId;
    private String resSender;
    private String title;
    private String status;
    private String fileName;
    private String filePath;

    public String getFileUrl() {
        return filePath != null ? "/api/doc/download/" + fileName : null;
    }

    static SimpleDateFormat simpleDataFormat = new SimpleDateFormat("yyyy-MM-dd");

    public static DocResponseDTO sOf(DocDetail docDetail, DocMaster docMaster, FileHistory fileHistory) {
        return DocResponseDTO.builder()
                .draftId(docDetail.getDraftId())
                .draftDate(simpleDataFormat.format(docMaster.getDraftDate()))
                .drafter(docMaster.getDrafter())
                .docId(docDetail.getDocId())
                .resSender(docDetail.getReceiver())
                .title(docDetail.getDocTitle())
                .status(docMaster.getStatus())
                .fileName(fileHistory != null ? fileHistory.getFileName() : "")
                .filePath(fileHistory != null ? fileHistory.getFilePath() : "")
                .build();
    }

    public static DocResponseDTO rOf(DocDetail docDetail, DocMaster docMaster, FileHistory fileHistory) {
        return DocResponseDTO.builder()
                .draftId(docDetail.getDraftId())
                .draftDate(simpleDataFormat.format(docMaster.getDraftDate()))
                .drafter(docMaster.getDrafter())
                .docId(docDetail.getDocId())
                .resSender(docDetail.getSender())
                .title(docDetail.getDocTitle())
                .status(docMaster.getStatus())
                .fileName(fileHistory != null ? fileHistory.getFileName() : "")
                .filePath(fileHistory != null ? fileHistory.getFilePath() : "")
                .build();
    }
}
