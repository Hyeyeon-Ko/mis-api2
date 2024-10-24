package kr.or.kmi.mis.api.toner.model.response;

import kr.or.kmi.mis.api.toner.model.entity.TonerMaster;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class TonerPendingListResponseDTO {

    private String draftId;
    private String title;
    private String instCd;
    private LocalDateTime draftDate;
    private String drafter;
    private LocalDateTime lastUpdateDate;   // 최종 수정일시
    private String lastUpdater;        // 최종 수정자
    private String applyStatus;
    private String docType;

    public static TonerPendingListResponseDTO of(TonerMaster tonerMaster, String updater) {
        return TonerPendingListResponseDTO.builder()
                .draftId(tonerMaster.getDraftId())
                .title(tonerMaster.getTitle())
                .instCd(tonerMaster.getInstCd())
                .draftDate(tonerMaster.getDraftDate())
                .drafter(tonerMaster.getDrafter())
                .lastUpdateDate(tonerMaster.getUpdtDt())
                .lastUpdater(updater)
                .applyStatus(tonerMaster.getStatus())
                .docType("토너신청")
                .build();
    }
}
