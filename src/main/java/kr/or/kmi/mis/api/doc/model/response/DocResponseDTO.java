package kr.or.kmi.mis.api.doc.model.response;

import kr.or.kmi.mis.api.doc.model.entity.DocDetail;
import kr.or.kmi.mis.api.doc.model.entity.DocMaster;
import kr.or.kmi.mis.api.file.model.entity.FileHistory;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Builder
@Data
@RequiredArgsConstructor
public class DocResponseDTO {

    private String draftId;
    private LocalDateTime draftDate;
    private String drafter;
    private String docId;
    private String sender;
    private String receiver;
    private String title;
    private String status;
    private String fileName;
    private String filePath;

    public String getFileUrl() {
        return filePath != null ? "/api/doc/download/" + fileName : null;
    }

//    static SimpleDateFormat simpleDataFormat = new SimpleDateFormat("yyyy-MM-dd");

    public DocResponseDTO(String draftId, LocalDateTime draftDate, String drafter, String docId, String sender, String receiver, String title, String status, String fileName, String filePath) {
        this.draftId = draftId;
        this.draftDate = draftDate;
        this.drafter = drafter;
        this.docId = docId;
        this.sender = sender;
        this.receiver = receiver;
        this.title = title;
        this.status = status;
        this.fileName = fileName;
        this.filePath = filePath;
    }


    public static DocResponseDTO sOf(DocDetail docDetail, DocMaster docMaster, FileHistory fileHistory) {
        return DocResponseDTO.builder()
                .draftId(docDetail.getDraftId())
//                .draftDate(simpleDataFormat.format(docMaster.getDraftDate()))
                .draftDate(docMaster.getDraftDate())
                .drafter(docMaster.getDrafter())
                .docId(docDetail.getDocId())
                .receiver(docDetail.getReceiver())
                .title(docDetail.getDocTitle())
                .status(docMaster.getStatus())
                .fileName(fileHistory != null ? fileHistory.getFileName() : "")
                .filePath(fileHistory != null ? fileHistory.getFilePath() : "")
                .build();
    }

    public static DocResponseDTO rOf(DocDetail docDetail, DocMaster docMaster, FileHistory fileHistory) {
        return DocResponseDTO.builder()
                .draftId(docDetail.getDraftId())
//                .draftDate(simpleDataFormat.format(docMaster.getDraftDate()))
                .draftDate(docMaster.getDraftDate())
                .drafter(docMaster.getDrafter())
                .docId(docDetail.getDocId())
                .sender(docDetail.getSender())
                .title(docDetail.getDocTitle())
                .status(docMaster.getStatus())
                .fileName(fileHistory != null ? fileHistory.getFileName() : "")
                .filePath(fileHistory != null ? fileHistory.getFilePath() : "")
                .build();
    }

}
