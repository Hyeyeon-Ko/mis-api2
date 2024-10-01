package kr.or.kmi.mis.api.corpdoc.model.response;

import kr.or.kmi.mis.api.corpdoc.model.entity.CorpDocMaster;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.sql.Timestamp;

@Data
@Builder
@AllArgsConstructor
public class CorpDocMasterResponseDTO {

    private String draftId;
    private Timestamp draftDate;
    private Timestamp respondDate;
    private String drafter;
    private String title;
    private String applyStatus;
    private String instCd;
    private String instNm;
    private String docType;

    public static CorpDocMasterResponseDTO of(CorpDocMaster corpDocMaster) {
        return CorpDocMasterResponseDTO.builder()
                .draftId(corpDocMaster.getDraftId())
                .draftDate(corpDocMaster.getDraftDate())
                .respondDate(corpDocMaster.getRespondDate())
                .drafter(corpDocMaster.getDrafter())
                .title(corpDocMaster.getTitle())
                .applyStatus(corpDocMaster.getStatus())
                .instCd(corpDocMaster.getInstCd())
                .docType("법인서류")
                .build();
    }
}
