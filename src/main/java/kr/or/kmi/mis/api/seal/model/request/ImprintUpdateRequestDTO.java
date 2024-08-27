package kr.or.kmi.mis.api.seal.model.request;

import lombok.Getter;

import java.sql.Timestamp;

@Getter
public class ImprintUpdateRequestDTO {

    private String submission;
    private String useDate;
    private String corporateSeal;
    private String facsimileSeal;
    private String companySeal;
    private String purpose;
    private String notes;
}
