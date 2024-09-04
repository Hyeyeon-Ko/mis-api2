package kr.or.kmi.mis.api.corpdoc.model.request;

import lombok.Getter;

@Getter
public class CorpDocUpdateRequestDTO {

    private String submission;
    private String purpose;
    private String useDate;
    private int certCorpseal;
    private int certCoregister;
    private int certUsesignet;
    private int warrant;
    private String type;
    private String notes;
}
