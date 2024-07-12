package kr.or.kmi.mis.api.bcd.model.response;

import kr.or.kmi.mis.api.bcd.model.entity.BcdMaster;
import lombok.*;

import java.sql.Timestamp;

@Builder
@Data
@AllArgsConstructor
public class BcdMasterResponseDTO {
    private Long draftId;
    private Long seqId;
    private String title;
    private Timestamp draftDate;
    private Timestamp respondDate;
    private Timestamp orderDate;
    private String drafter;
    private String instNm;
    private String applyStatus;
    private String lastUpdtId;
    private Timestamp lastUpdtDate;

    // BcdMaster Entity -> BcdMaster response Dto
    public static BcdMasterResponseDTO of(BcdMaster bcdMaster, Long seqId, String instNm, String lastUpdtId, Timestamp lastUpdtDate) {

        return BcdMasterResponseDTO.builder()
                .draftId(bcdMaster.getDraftId())
                .seqId(seqId)
                .title(bcdMaster.getTitle())
                .instNm(instNm)
                .draftDate(bcdMaster.getDraftDate())
                .respondDate(bcdMaster.getRespondDate())
                .orderDate(bcdMaster.getOrderDate())
                .drafter(bcdMaster.getDrafter())
                .applyStatus(bcdMaster.getStatus())
                .lastUpdtId(lastUpdtId)
                .lastUpdtDate(lastUpdtDate)
                .build();
    }

}
