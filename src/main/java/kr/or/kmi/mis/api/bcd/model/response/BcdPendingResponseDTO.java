package kr.or.kmi.mis.api.bcd.model.response;

import lombok.*;

import java.sql.Timestamp;

@Builder
@Data
@AllArgsConstructor
public class BcdPendingResponseDTO {
    private Long draftId;
    private Long seqId;
    private String title;
    private String instNm;
    private Timestamp draftDate;
    private String drafter;
    private Timestamp lastUpdateDate;   // 최종 수정일시
    private String lastUpdateId;        // 최종 수정자
    private String applyStatus;

    // BcdMaster Entity -> BcdPending response Dto
    public static BcdPendingResponseDTO of(BcdMasterResponseDTO bcdMasterResponseDTO) {

        return BcdPendingResponseDTO.builder()
                .draftId(bcdMasterResponseDTO.getDraftId())
                .seqId(bcdMasterResponseDTO.getSeqId())
                .title(bcdMasterResponseDTO.getTitle())
                .instNm(bcdMasterResponseDTO.getInstNm())
                .draftDate(bcdMasterResponseDTO.getDraftDate())
                .drafter(bcdMasterResponseDTO.getDrafter())
                .applyStatus(bcdMasterResponseDTO.getApplyStatus())
                .build();
    }
}