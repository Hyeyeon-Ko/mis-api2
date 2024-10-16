package kr.or.kmi.mis.api.apply.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class PendingCountResponseDTO {

    private int bcdPendingCount;
    private int docPendingCount;
    private int corpDocPendingCount;
    private int sealPendingCount;
    private int corpDocIssuePendingCount;
    private int orderPendingCount;
    private int docstoragePendingCount;

    public static PendingCountResponseDTO of(int bcdPendingCount, int docPendingCount, int corpDocPendingCount, int sealPendingCount,
                                             int corpDocIssuePendingCount, int orderPendingCount, int docstoragePendingCount) {
        return PendingCountResponseDTO.builder()
                .bcdPendingCount(bcdPendingCount)
                .docPendingCount(docPendingCount)
                .corpDocPendingCount(corpDocPendingCount)
                .sealPendingCount(sealPendingCount)
                .corpDocIssuePendingCount(corpDocIssuePendingCount)
                .orderPendingCount(orderPendingCount)
                .docstoragePendingCount(docstoragePendingCount)
                .build();
    }
}
