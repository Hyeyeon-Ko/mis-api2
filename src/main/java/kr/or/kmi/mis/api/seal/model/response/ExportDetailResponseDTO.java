package kr.or.kmi.mis.api.seal.model.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ExportDetailResponseDTO {

    private String draftId;
    private String division;
    private String submission;
    private String expNm;
    private String expDate;
    private String returnDate;
    private String corporateSeal;
    private String facsimileSeal;
    private String companySeal;
    private String purpose;
    private String notes;
    private String fileName;
    private String filePath;
}
