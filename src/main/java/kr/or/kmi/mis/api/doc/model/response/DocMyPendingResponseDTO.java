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
public class DocMyPendingResponseDTO {

    private Long draftId;
    private String title;
    private Timestamp draftDate;
    private String drafter;
    private Timestamp lastUpdateDate;
    private String lastUpdater;
    private String applyStatus;
    private String docType;

    public static DocMyPendingResponseDTO of(DocMaster docMaster) {
        return DocMyPendingResponseDTO.builder()
                .draftId(docMaster.getDraftId())
                .title(docMaster.getTitle())
                .draftDate(docMaster.getDraftDate())
                .drafter(docMaster.getDrafter())
                .lastUpdateDate(docMaster.getUpdtDt())
                .lastUpdater(docMaster.getDrafter())
                .applyStatus(docMaster.getStatus())
                .docType("문서수발신")
                .build();
    }
}
