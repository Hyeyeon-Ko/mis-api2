package kr.or.kmi.mis.api.doc.model.response;

import kr.or.kmi.mis.api.doc.model.entity.DocMaster;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Builder
@Data
@AllArgsConstructor
public class DocMasterResponseDTO {

    private String draftId;
    private String title;
    private String instCd;
    private String instNm;
    private LocalDateTime draftDate;
    private LocalDateTime respondDate;
    private String drafter;
    private String approver;
    private String applyStatus;
    private String docType;
    private String approverChain;
    private int currentApproverIndex;

    public static DocMasterResponseDTO of(DocMaster docMaster, String division) {

        String docType = "A".equals(division) ? "문서수신" : "문서발신";

        return DocMasterResponseDTO.builder()
                .draftId(docMaster.getDraftId())
                .title(docMaster.getTitle())
                .instCd(docMaster.getInstCd())
                .draftDate(docMaster.getDraftDate())
                .respondDate(docMaster.getRespondDate())
                .drafter(docMaster.getDrafter())
                .approver(docMaster.getApprover())
                .applyStatus(docMaster.getStatus())
                .docType(docType)
                .build();
    }
}
