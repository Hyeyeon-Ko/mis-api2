package kr.or.kmi.mis.api.apply.model.request;

import lombok.Data;

@Data
public class ConfirmRequestDTO {

    private String userId;
    private String rejectReason;
}
