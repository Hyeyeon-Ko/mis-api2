package kr.or.kmi.mis.api.doc.model.response;

import kr.or.kmi.mis.api.doc.model.entity.DocMaster;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@Builder
@Data
@AllArgsConstructor
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

    public static DocPendingResponseDTO of(DocMaster docMaster, String division) {

        String docType = "A".equals(division) ? "문서수신" : "문서발신";

        return DocPendingResponseDTO.builder()
                .draftId(docMaster.getDraftId())
                .title(docMaster.getTitle())
                .instCd(docMaster.getInstCd())
                .draftDate(docMaster.getDraftDate())
                .drafter(docMaster.getDrafter())
                .lastUpdateDate(docMaster.getUpdtDt())
                .lastUpdater(docMaster.getDrafter())
                .applyStatus(docMaster.getStatus())
                .docType(docType)
                .build();
    }
}
