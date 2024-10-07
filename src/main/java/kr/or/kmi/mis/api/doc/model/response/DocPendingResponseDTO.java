package kr.or.kmi.mis.api.doc.model.response;

import kr.or.kmi.mis.api.doc.model.entity.DocMaster;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@Builder
@Data
public class DocPendingResponseDTO {

    private String draftId;
    private String title;
    private String instCd;
    private String instNm;
    private LocalDateTime draftDate;
    private String drafter;
    private LocalDateTime lastUpdateDate;   // 최종 수정일시
    private String lastUpdater;        // 최종 수정자
    private String applyStatus;
    private String docType;

    public DocPendingResponseDTO(String draftId, String title, String instCd, String instNm, LocalDateTime draftDate, String drafter, LocalDateTime lastUpdateDate, String lastUpdater, String applyStatus, String docType, String approverChain, int currentApproverIndex) {
        this.draftId = draftId;
        this.title = title;
        this.instCd = instCd;
        this.instNm = instNm;
        this.draftDate = draftDate;
        this.drafter = drafter;
        this.lastUpdateDate = lastUpdateDate;
        this.lastUpdater = lastUpdater;
        this.applyStatus = applyStatus;
        this.docType = docType;
        this.approverChain = approverChain;
        this.currentApproverIndex = currentApproverIndex;
    }

    private String approverChain;
    private int currentApproverIndex;

    public static DocPendingResponseDTO of(DocMaster docMaster, String division, String updater) {

        String docType = "A".equals(division) ? "문서수신" : "문서발신";

        return DocPendingResponseDTO.builder()
                .draftId(docMaster.getDraftId())
                .title(docMaster.getTitle())
                .instCd(docMaster.getInstCd())
                .draftDate(docMaster.getDraftDate())
                .drafter(docMaster.getDrafter())
                .lastUpdateDate(docMaster.getUpdtDt())
                .lastUpdater(updater)
                .applyStatus(docMaster.getStatus())
                .docType(docType)
                .build();
    }
}
