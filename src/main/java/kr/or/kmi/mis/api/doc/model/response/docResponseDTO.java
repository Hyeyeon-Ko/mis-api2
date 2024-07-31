package kr.or.kmi.mis.api.doc.model.response;

import kr.or.kmi.mis.api.doc.model.entity.DocDetail;
import kr.or.kmi.mis.api.doc.model.entity.DocMaster;
import lombok.Builder;
import lombok.Data;

import java.text.SimpleDateFormat;

@Builder
@Data
public class docResponseDTO {

    private Long draftId;
    private String draftDate;
    private String drafter;
    private String docId;
    private String resSender;
    private String title;
    private String status;

    static SimpleDateFormat simpleDataFormat = new SimpleDateFormat("yyyy-MM-dd");

    public static docResponseDTO sOf(DocDetail docDetail, DocMaster docMaster) {
        return docResponseDTO.builder()
                .draftId(docDetail.getDraftId())
                .draftDate(simpleDataFormat.format(docMaster.getDraftDate()))
                .drafter(docMaster.getDrafter())
                .docId(docDetail.getDocId())
                .resSender(docDetail.getReceiver())
                .title(docDetail.getDocTitle())
                .status(docMaster.getStatus())
                .build();
    }

    public static docResponseDTO rOf(DocDetail docDetail, DocMaster docMaster) {
        return docResponseDTO.builder()
                .draftId(docDetail.getDraftId())
                .draftDate(simpleDataFormat.format(docMaster.getDraftDate()))
                .drafter(docMaster.getDrafter())
                .docId(docDetail.getDocId())
                .resSender(docDetail.getReceiver())
                .title(docDetail.getDocTitle())
                .status(docMaster.getStatus())
                .build();
    }
}
