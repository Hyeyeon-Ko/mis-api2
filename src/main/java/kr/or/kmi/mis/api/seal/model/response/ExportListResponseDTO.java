package kr.or.kmi.mis.api.seal.model.response;

import kr.or.kmi.mis.api.seal.model.entity.SealExportDetail;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ExportListResponseDTO {

    // TODO: 인장관리대장에 불러와야할 데이터 다시 확인!!
    private String draftId;
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

    public static ExportListResponseDTO of(SealExportDetail sealExportDetail) {
        return ExportListResponseDTO.builder()
                .draftId(sealExportDetail.getRgstrId())
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
