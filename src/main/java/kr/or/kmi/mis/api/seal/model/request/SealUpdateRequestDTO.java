package kr.or.kmi.mis.api.seal.model.request;

import lombok.Data;

@Data
public class SealUpdateRequestDTO {

    private String drafterId;
    private String sealNm;
    private String sealImage;
    private String useDept;
    private String purpose;
    private String manager;
    private String subManager;

}
