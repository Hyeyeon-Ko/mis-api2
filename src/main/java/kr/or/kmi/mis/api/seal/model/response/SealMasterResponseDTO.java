package kr.or.kmi.mis.api.seal.model.response;

import kr.or.kmi.mis.api.seal.model.entity.SealMaster;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class SealMasterResponseDTO {

    private String draftId;
    private String instCd;
    private String instNm;
    private String title;
    private LocalDateTime draftDate;
    private LocalDateTime respondDate;
    private String drafter;
    private String applyStatus;
    private String docType;

    public SealMasterResponseDTO(String draftId, String instCd, String instNm, String title, LocalDateTime draftDate, LocalDateTime respondDate, String drafter, String applyStatus, String docType) {
        this.draftId = draftId;
        this.instCd = instCd;
        this.instNm = instNm;
        this.title = title;
        this.draftDate = draftDate;
        this.respondDate = respondDate;
        this.drafter = drafter;
        this.applyStatus = applyStatus;
        this.docType = docType;
    }

    public static SealMasterResponseDTO of(SealMaster sealMaster) {

        String docType = "A".equals(sealMaster.getDivision()) ? "인장신청(날인)" : "인장신청(반출)";

        return SealMasterResponseDTO.builder()
                .draftId(sealMaster.getDraftId())
                .instCd(sealMaster.getInstCd())
                .title(sealMaster.getTitle())
                .draftDate(sealMaster.getDraftDate())
                .respondDate(sealMaster.getRespondDate())
                .drafter(sealMaster.getDrafter())
                .applyStatus(sealMaster.getStatus())
                .docType(docType)
                .build();
    }
}
