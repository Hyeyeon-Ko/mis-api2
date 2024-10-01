package kr.or.kmi.mis.api.confirm.model.request;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@Getter
@RequiredArgsConstructor
public class BcdDisapproveRequestDTO {

    private String disapproverId;
    private String disapprover;
    private String rejectReason;
    private LocalDateTime respondDate;
    private String status;

    @Builder
    public BcdDisapproveRequestDTO(String disapproverId, String disapprover, String rejectReason, LocalDateTime respondDate, String status) {
        this.disapproverId = disapproverId;
        this.disapprover = disapprover;
        this.rejectReason = rejectReason;
        this.respondDate = respondDate;
        this.status = status;
    }
}
