package kr.or.kmi.mis.api.seal.model.request;

import lombok.Getter;

@Getter
public class ExportUpdateRequestDTO {

    private String submission;
    private String expNm;
    private String expDate;
    private String returnDate;
    private String corporateSeal;
    private String facsimileSeal;
    private String companySeal;
    private String purpose;
    private String notes;
}
