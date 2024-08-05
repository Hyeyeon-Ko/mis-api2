package kr.or.kmi.mis.api.order.model.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class EmailSettingsResponseDTO {
    private String fromEmail;
    private String toEmail;
}
