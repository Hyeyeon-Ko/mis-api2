package kr.or.kmi.mis.api.doc.model.request;

import lombok.Getter;

import java.sql.Timestamp;

@Getter
public class DocUpdateRequestDTO {

    private String division;
    private String sender;
    private String receiver;
    private String docTitle;
    private String purpose;
}
