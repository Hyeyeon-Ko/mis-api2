package kr.or.kmi.mis.api.order.model.request;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Data
@RequiredArgsConstructor
public class OrderRequestDTO {

    private List<String> draftIds;
    private String emailSubject;
    private String emailBody;
    private String fileName;
    private String fromEmail;
    private String toEmail;
    private String password;
}
