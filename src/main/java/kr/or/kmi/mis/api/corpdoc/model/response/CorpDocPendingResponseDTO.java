package kr.or.kmi.mis.api.corpdoc.model.response;

import kr.or.kmi.mis.api.corpdoc.model.entity.CorpDocDetail;
import kr.or.kmi.mis.api.corpdoc.model.entity.CorpDocMaster;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.sql.Timestamp;

@Builder
@Data
@AllArgsConstructor
public class CorpDocPendingResponseDTO {
    private Long draftId;
    private String title;
    private String instCd;
    private Timestamp draftDate;
    private String drafter;
    private Timestamp lastUpdateDate;
    private String lastUpdater;
    private String applyStatus;
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
