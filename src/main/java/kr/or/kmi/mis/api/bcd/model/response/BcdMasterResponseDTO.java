package kr.or.kmi.mis.api.bcd.model.response;

import kr.or.kmi.mis.api.bcd.model.entity.BcdMaster;
import lombok.*;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@Builder
@Data
@AllArgsConstructor
public class BcdMasterResponseDTO {
    private String draftId;
    private String title;
    private String instCd;
    private String instNm;
    private LocalDateTime draftDate;
    private LocalDateTime respondDate;
    private LocalDateTime orderDate;
    private String drafter;
    private String applyStatus;
    private String lastUpdtId;
    private LocalDateTime lastUpdtDate;
    private String docType;
    private String approverChain;
    private int currentApproverIndex;

    // BcdMaster Entity -> BcdMaster response Dto
    public static BcdMasterResponseDTO of(BcdMaster bcdMaster, String instCd, String instNm) {

        return BcdMasterResponseDTO.builder()
                .draftId(bcdMaster.getDraftId())
                .title(bcdMaster.getTitle())
                .instCd(instCd)
                .instNm(instNm)
                .draftDate(bcdMaster.getDraftDate())
                .respondDate(bcdMaster.getRespondDate())
                .orderDate(bcdMaster.getOrderDate())
                .drafter(bcdMaster.getDrafter())
                .applyStatus(bcdMaster.getStatus())
                .docType("명함신청")
                .build();
    }

}
