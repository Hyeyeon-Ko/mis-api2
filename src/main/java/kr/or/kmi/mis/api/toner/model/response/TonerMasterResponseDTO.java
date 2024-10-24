package kr.or.kmi.mis.api.toner.model.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TonerMasterResponseDTO {

    private String draftId;
    private String title;
    private String instCd;
    private String instNm;
    private LocalDateTime draftDate;
    private LocalDateTime respondDate;
    private LocalDateTime orderDate;
    private String drafter;
    private String applyStatus;
    private String docType;

    @Builder
    public TonerMasterResponseDTO(String draftId, String title, String instCd, String instNm, LocalDateTime draftDate, LocalDateTime respondDate, LocalDateTime orderDate, String drafter, String applyStatus, String docType) {
        this.draftId = draftId;
        this.title = title;
        this.instCd = instCd;
        this.instNm = instNm;
        this.draftDate = draftDate;
        this.respondDate = respondDate;
        this.orderDate = orderDate;
        this.drafter = drafter;
        this.applyStatus = applyStatus;
        this.docType = docType;
    }
}
