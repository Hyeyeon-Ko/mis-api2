package kr.or.kmi.mis.api.confirm.model.request;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.sql.Timestamp;

@Getter
@RequiredArgsConstructor
public class BcdApproveRequestDTO {

    private String approverId;
    private String approver;
    private Timestamp respondDate;
    private String status;

    @Builder
    public BcdApproveRequestDTO(String approverId, String approver, Timestamp respondDate, String status) {
        this.approverId = approverId;
        this.approver = approver;
        this.respondDate = respondDate;
        this.status = status;
    }
}
