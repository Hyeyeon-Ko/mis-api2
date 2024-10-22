package kr.or.kmi.mis.api.toner.model.request;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Getter
@RequiredArgsConstructor
public class TonerApproverRequestDTO {

    private String approverId;
    private String approver;
    private LocalDateTime respondDate;
    private String status;

    @Builder
    public TonerApproverRequestDTO(String approverId, String approver, LocalDateTime respondDate, String status) {
        this.approverId = approverId;
        this.approver = approver;
        this.respondDate = respondDate;
        this.status = status;
    }
}
