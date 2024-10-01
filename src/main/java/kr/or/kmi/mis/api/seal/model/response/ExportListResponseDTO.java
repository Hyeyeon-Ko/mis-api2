package kr.or.kmi.mis.api.seal.model.response;

import kr.or.kmi.mis.api.file.model.entity.FileDetail;
import kr.or.kmi.mis.api.file.model.entity.FileHistory;
import kr.or.kmi.mis.api.seal.model.entity.SealExportDetail;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ExportListResponseDTO {

    private String draftId;
    private String drafter;
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

    public static ExportListResponseDTO of(SealExportDetail sealExportDetail, String drafter, FileHistory fileHistory) {
        return ExportListResponseDTO.builder()
                .draftId(sealExportDetail.getDraftId())
                .drafter(drafter)
                .submission(sealExportDetail.getSubmission())
                .expNm(sealExportDetail.getExpNm())
                .expDate(sealExportDetail.getExpDate())
                .returnDate(sealExportDetail.getReturnDate())
                .corporateSeal(sealExportDetail.getCorporateSeal())
                .facsimileSeal(sealExportDetail.getFacsimileSeal())
                .companySeal(sealExportDetail.getCompanySeal())
                .purpose(sealExportDetail.getPurpose())
                .notes(sealExportDetail.getNotes())
                .fileName(fileHistory != null ? fileHistory.getFileName() : "")
                .filePath(fileHistory != null ? fileHistory.getFilePath() : "")
                .build();
    }
}
