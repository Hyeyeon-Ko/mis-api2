package kr.or.kmi.mis.api.corpdoc.model.response;

import kr.or.kmi.mis.api.corpdoc.model.entity.CorpDocDetail;
import kr.or.kmi.mis.api.corpdoc.model.entity.CorpDocMaster;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@Builder
@Data
public class CorpDocPendingResponseDTO {
    private String draftId;
    private String title;
    private String instCd;
    private String instNm;
    private LocalDateTime draftDate;
    private String drafter;
    private LocalDateTime lastUpdateDate;
    private String lastUpdater;
    private String applyStatus;

    public CorpDocPendingResponseDTO(String draftId, String title, String instCd, String instNm, LocalDateTime draftDate, String drafter, LocalDateTime lastUpdateDate, String lastUpdater, String applyStatus, String docType) {
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
    }

    private String docType;

    public static CorpDocPendingResponseDTO of(CorpDocMaster corpDocMaster, CorpDocDetail corpDocDetail) {

        return CorpDocPendingResponseDTO.builder()
                .draftId(corpDocMaster.getDraftId())
                .instCd(corpDocMaster.getInstCd())
                .title(corpDocMaster.getTitle())
                .draftDate(corpDocMaster.getDraftDate())
                .drafter(corpDocMaster.getDrafter())
                .lastUpdateDate(corpDocDetail.getUpdtDt())
                .lastUpdater(corpDocDetail.getUpdtrId())
                .applyStatus(corpDocMaster.getStatus())
                .docType("법인서류")
                .build();
    }
}
