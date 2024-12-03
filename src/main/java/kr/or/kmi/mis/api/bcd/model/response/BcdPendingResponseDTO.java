package kr.or.kmi.mis.api.bcd.model.response;

import kr.or.kmi.mis.api.bcd.model.entity.BcdDetail;
import kr.or.kmi.mis.api.bcd.model.entity.BcdMaster;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Builder
@Data
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

    public BcdPendingResponseDTO(String draftId, String title, String instCd, String instNm, LocalDateTime draftDate, String drafter, LocalDateTime lastUpdateDate, String lastUpdater, String applyStatus, String docType) {
        this.draftId = draftId;
        this.title = title;
        this.instCd = instCd;
        this.instNm = instNm;
        this.draftDate = draftDate;
        this.drafter = drafter;
        this.lastUpdateDate = lastUpdateDate;
        this.lastUpdater = lastUpdater;
        this.applyStatus = applyStatus;
        this.docType = docType;
    }

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