package kr.or.kmi.mis.api.seal.model.response;

import kr.or.kmi.mis.api.seal.model.entity.SealMaster;
import lombok.Builder;
import lombok.Data;

import java.sql.Timestamp;

@Data
@Builder
public class SealPendingResponseDTO {

    private String draftId;
    private String title;
    private String instCd;
    private String instNm;
    private Timestamp draftDate;
    private String drafter;
    private Timestamp lastUpdateDate;
    private String lastUpdater;
    private String applyStatus;
    private String docType;

    public static SealPendingResponseDTO of(SealMaster sealMaster) {

        String docType = "A".equals(sealMaster.getDivision()) ? "인장신청(날인)" : "인장신청(반출)";

        return SealPendingResponseDTO.builder()
                .draftId(sealMaster.getDraftId())
                .title(sealMaster.getTitle())
                .instCd(sealMaster.getInstCd())
                .draftDate(sealMaster.getDraftDate())
                .drafter(sealMaster.getDrafter())
                .lastUpdateDate(sealMaster.getUpdtDt())
                .lastUpdater(sealMaster.getDrafter())
                .applyStatus(sealMaster.getStatus())
                .docType(docType)
                .build();
    }

}
