package kr.or.kmi.mis.api.bcd.model.response;

import kr.or.kmi.mis.api.bcd.model.entity.BcdMaster;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Builder
@Getter
@Setter
@AllArgsConstructor
public class BcdMasterResponseDTO {
    private Long draftId;
    private Long seqId;
    private String title;
    private Timestamp draftDate;
    private Timestamp respondDate;
    private Timestamp orderDate;
    private String drafter;
    private String applyStatus;
    private String lastUpdtId;
    private Timestamp lastUpdtDate;

    // BcdMaster Entity -> BcdMaster response Dto
    public static BcdMasterResponseDTO of(BcdMaster bcdMaster, Long seqId, String lastUpdtId, Timestamp lastUpdtDate) {

        return BcdMasterResponseDTO.builder()
                .draftId(bcdMaster.getDraftId())
                .seqId(seqId)
                .title(bcdMaster.getTitle())
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
