package kr.or.kmi.mis.api.seal.model.response;

import kr.or.kmi.mis.api.seal.model.entity.SealExportDetail;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SealExportDetailResponseDTO {

    private Long draftId;
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

    public static SealExportDetailResponseDTO of(SealExportDetail sealExportDetail) {
        return SealExportDetailResponseDTO.builder()
                .draftId(sealExportDetail.getDraftId())
                .submission(sealExportDetail.getSubmission())
                .useDept(sealExportDetail.getUseDept())
                .expNm(sealExportDetail.getExpNm())
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
