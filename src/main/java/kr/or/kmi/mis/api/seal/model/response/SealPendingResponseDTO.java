package kr.or.kmi.mis.api.seal.model.response;

import kr.or.kmi.mis.api.seal.model.entity.SealMaster;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class SealPendingResponseDTO {

    private String draftId;
    private String title;
    private String instCd;
    private String instNm;
    private LocalDateTime draftDate;
    private String drafter;
    private LocalDateTime lastUpdateDate;
    private String lastUpdater;
    private String applyStatus;
    private String docType;

    public SealPendingResponseDTO(String draftId, String title, String instCd, String instNm, LocalDateTime draftDate, String drafter, LocalDateTime lastUpdateDate, String lastUpdater, String applyStatus, String docType) {
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

    public static SealPendingResponseDTO of(SealMaster sealMaster, String updater) {

        String docType = "A".equals(sealMaster.getDivision()) ? "인장신청(날인)" : "인장신청(반출)";

        return SealPendingResponseDTO.builder()
                .draftId(sealMaster.getDraftId())
                .title(sealMaster.getTitle())
                .instCd(sealMaster.getInstCd())
                .draftDate(sealMaster.getDraftDate())
                .drafter(sealMaster.getDrafter())
                .lastUpdateDate(sealMaster.getUpdtDt())
                .lastUpdater(updater)
                .applyStatus(sealMaster.getStatus())
                .docType(docType)
                .build();
    }

}
