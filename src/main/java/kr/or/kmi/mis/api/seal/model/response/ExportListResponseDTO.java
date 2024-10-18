package kr.or.kmi.mis.api.seal.model.response;

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

    public ExportListResponseDTO(String draftId, String drafter, String submission, String expNm, String expDate, String returnDate, String corporateSeal, String facsimileSeal, String companySeal, String purpose, String notes, String fileName, String filePath) {
        this.draftId = draftId;
        this.drafter = drafter;
        this.submission = submission;
        this.expNm = expNm;
        this.expDate = expDate;
        this.returnDate = returnDate;
        this.corporateSeal = corporateSeal;
        this.facsimileSeal = facsimileSeal;
        this.companySeal = companySeal;
        this.purpose = purpose;
        this.notes = notes;
        this.fileName = fileName;
        this.filePath = filePath;
    }

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
