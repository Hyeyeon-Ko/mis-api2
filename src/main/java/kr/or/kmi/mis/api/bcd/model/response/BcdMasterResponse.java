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
public class BcdMasterResponse {
    private Long draftId;
    private Long seqId;
    private String title;
    private Timestamp draftDate;
    private Timestamp respondDate;
    private Timestamp orderDate;
    private String drafter;
    private String applyStatus;

    // BcdMaster Entity -> BcdMaster response Dto
    public static BcdMasterResponse of(BcdMaster bcdMaster, Long seqId) {

        return BcdMasterResponse.builder()
                .draftId(bcdMaster.getDraftId())
                .seqId(seqId)
                .title(bcdMaster.getTitle())
                .draftDate(bcdMaster.getDraftDate())
                .respondDate(bcdMaster.getRespondDate())
                .orderDate(bcdMaster.getOrderDate())
                .drafter(bcdMaster.getDrafter())
                .applyStatus(bcdMaster.getStatus())
                .build();
    }

}
