package kr.or.kmi.mis.api.confirm.model.request;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.sql.Timestamp;

@Getter
@RequiredArgsConstructor
public class BcdDisapproveRequestDTO {

    private String disapproverId;
    private String disapprover;
    private String rejectReason;
    private Timestamp respondDate;
    private String status;

    @Builder
    public BcdDisapproveRequestDTO(String disapproverId, String disapprover, String rejectReason, Timestamp respondDate, String status) {
        this.disapproverId = disapproverId;
        this.disapprover = disapprover;
        this.rejectReason = rejectReason;
        this.respondDate = respondDate;
        this.status = status;
    }
}
