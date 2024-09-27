package kr.or.kmi.mis.api.seal.model.response;

import kr.or.kmi.mis.api.seal.model.entity.SealExportDetail;
import kr.or.kmi.mis.api.seal.model.entity.SealMaster;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ExportDetailResponseDTO {

    private Long draftId;
    private String division;
    private String submission;
    private String useDept;
    private String expNm;
    private String expDate;
    private String returnDate;
    private String corporateSeal;
    private String facsimileSeal;
    private String companySeal;
    private String purpose;
    private String fileName;
    private String filePath;
}
