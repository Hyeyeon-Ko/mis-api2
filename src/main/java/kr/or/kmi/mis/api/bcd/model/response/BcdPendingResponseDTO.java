package kr.or.kmi.mis.api.bcd.model.response;

import kr.or.kmi.mis.api.bcd.model.entity.BcdDetail;
import kr.or.kmi.mis.api.bcd.model.entity.BcdMaster;
import lombok.*;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@Builder
@Data
@AllArgsConstructor
public class BcdPendingResponseDTO {
    private String draftId;
    private String title;
    private String instCd;
    private String instNm;
    private LocalDateTime draftDate;
    private String drafter;
    private LocalDateTime lastUpdateDate;   // 최종 수정일시
    private String lastUpdater;        // 최종 수정자
    private String applyStatus;
    private String docType;
    private String approverChain;
    private int currentApproverIndex;

    // BcdMaster Entity -> BcdPending response Dto
    public static BcdPendingResponseDTO of(BcdMaster bcdMaster, BcdDetail bcdDetail) {

        return BcdPendingResponseDTO.builder()
                .draftId(bcdMaster.getDraftId())
                .instCd(bcdDetail.getInstCd())
                .title(bcdMaster.getTitle())
                .draftDate(bcdMaster.getDraftDate())
                .drafter(bcdMaster.getDrafter())
                .lastUpdateDate(bcdDetail.getLastupdtDate())
                .lastUpdater(bcdDetail.getLastUpdtr())
                .applyStatus(bcdMaster.getStatus())
                .docType("명함신청")
                .build();
    }
}