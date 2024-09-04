package kr.or.kmi.mis.api.seal.model.response;

import kr.or.kmi.mis.api.seal.model.entity.SealExportDetail;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ExportListResponseDTO {

    private String draftId;
    private String drafter;
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

    public static ExportListResponseDTO of(SealExportDetail sealExportDetail, String drafter) {
        return ExportListResponseDTO.builder()
                .draftId(sealExportDetail.getRgstrId())
                .drafter(drafter)
                .submission(sealExportDetail.getSubmission())
                .useDept(sealExportDetail.getUseDept())
                .expNm(sealExportDetail.getFacsimileSeal())
                .expDate(sealExportDetail.getExpDate())
                .returnDate(sealExportDetail.getReturnDate())
                .corporateSeal(sealExportDetail.getCorporateSeal())
                .facsimileSeal(sealExportDetail.getFacsimileSeal())
                .companySeal(sealExportDetail.getCompanySeal())
                .purpose(sealExportDetail.getPurpose())
                .fileName(sealExportDetail.getFileName())
                .filePath(sealExportDetail.getFilePath())
                .build();
    }
}
