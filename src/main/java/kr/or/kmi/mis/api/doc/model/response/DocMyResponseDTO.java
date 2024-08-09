package kr.or.kmi.mis.api.doc.model.response;

import kr.or.kmi.mis.api.doc.model.entity.DocMaster;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.sql.Timestamp;

@Builder
@Data
@AllArgsConstructor
public class DocMyResponseDTO {

    private Long draftId;
    private String title;
    private Timestamp draftDate;
    private Timestamp respondDate;
    private String drafter;
    private String approver;
    private String applyStatus;
    private String docType;

    public static DocMyResponseDTO of(DocMaster docMaster, String division) {

        String docType = "A".equals(division) ? "문서수신" : "문서발신";

        return DocMyResponseDTO.builder()
                .draftId(docMaster.getDraftId())
                .title(docMaster.getTitle())
                .draftDate(docMaster.getDraftDate())
                .respondDate(docMaster.getRespondDate())
                .drafter(docMaster.getDrafter())
                .approver(docMaster.getApprover())
                .applyStatus(docMaster.getStatus())
                .docType(docType)
                .build();
    }
}