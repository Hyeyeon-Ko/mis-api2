package kr.or.kmi.mis.api.doc.model.response;

import kr.or.kmi.mis.api.doc.model.entity.DocDetail;
import kr.or.kmi.mis.api.doc.model.entity.DocMaster;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.sql.Timestamp;

@Builder
@Data
@AllArgsConstructor
public class DocDetailResponseDTO {

    private Timestamp draftDate;
    private Timestamp lastUpdateDate;
    private String division;
    private String receiver;
    private String sender;
    private String docTitle;
    private String purpose;

    public static DocDetailResponseDTO of(DocMaster docMaster, DocDetail docDetail) {
        return DocDetailResponseDTO.builder()
                .draftDate(docMaster.getDraftDate())
                .lastUpdateDate(docDetail.getUpdtDt())
                .division(docDetail.getDivision())
                .receiver(docDetail.getReceiver())
                .sender(docDetail.getSender())
                .docTitle(docDetail.getDocTitle())
                .purpose(docDetail.getPurpose())
                .build();
    }
}
