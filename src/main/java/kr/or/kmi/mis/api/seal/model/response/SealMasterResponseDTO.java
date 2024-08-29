package kr.or.kmi.mis.api.seal.model.response;

import kr.or.kmi.mis.api.seal.model.entity.SealMaster;
import lombok.Builder;
import lombok.Data;

import java.sql.Timestamp;

@Data
@Builder
public class SealMasterResponseDTO {

    private Long draftId;
    private String instCd;
    private String title;
    private Timestamp draftDate;
    private Timestamp respondDate;
    private String drafter;
    private String applyStatus;
    private String lastUpdtId;
    private String lastUpdtDate;
    private String docType;

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
