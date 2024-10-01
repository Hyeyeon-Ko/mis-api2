package kr.or.kmi.mis.api.seal.model.entity;

import jakarta.persistence.*;
import kr.or.kmi.mis.api.seal.model.request.ExportRequestDTO;
import kr.or.kmi.mis.api.seal.model.request.ExportUpdateRequestDTO;
import kr.or.kmi.mis.cmm.model.entity.BaseSystemFieldEntity;
import lombok.*;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@ToString
@Entity
@Table(name = "hrmtexpd")
public class SealExportDetail extends BaseSystemFieldEntity {

    @Id
    @Column(nullable = false, length = 12)
    private String draftId;

    @Column(length = 50)
    private String submission;

    @Column(length = 20)
    private String expNm;

    @Column(length = 20)
    private String expDate;

    @Column(length = 20)
    private String returnDate;

    @Column(length = 20)
    private String corporateSeal;

    @Column(length = 20)
    private String facsimileSeal;

    @Column(length = 20)
    private String companySeal;

    @Column(length = 1000)
    private String purpose;

    @Column(length = 1000)
    private String notes;

    @Builder
    public SealExportDetail(String draftId, String submission, String expNm, String expDate, String returnDate,
                            String corporateSeal, String facsimileSeal, String companySeal, String purpose, String notes) {
        this.draftId = draftId;
        this.submission = submission;
        this.expNm = expNm;
        this.expDate = expDate;
        this.returnDate = returnDate;
        this.corporateSeal = corporateSeal;
        this.facsimileSeal = facsimileSeal;
        this.companySeal = companySeal;
        this.purpose = purpose;
        this.notes = notes;
    }

    public void update(ExportRequestDTO exportRequestDTO) {
        this.submission = exportRequestDTO.getSubmission();
        this.expNm = exportRequestDTO.getExpNm();
        this.expDate = exportRequestDTO.getExpDate();
        this.returnDate = exportRequestDTO.getReturnDate();
        this.corporateSeal = exportRequestDTO.getCorporateSeal();
        this.facsimileSeal = exportRequestDTO.getFacsimileSeal();
        this.companySeal = exportRequestDTO.getCompanySeal();
        this.purpose = exportRequestDTO.getPurpose();
        this.notes = exportRequestDTO.getNotes();
    }

    public void updateFile(ExportUpdateRequestDTO exportUpdateRequestDTO) {
        this.submission = exportUpdateRequestDTO.getSubmission();
        this.expNm = exportUpdateRequestDTO.getExpNm();
        this.expDate = exportUpdateRequestDTO.getExpDate();
        this.returnDate = exportUpdateRequestDTO.getReturnDate();
        this.corporateSeal = exportUpdateRequestDTO.getCorporateSeal();
        this.facsimileSeal = exportUpdateRequestDTO.getFacsimileSeal();
        this.companySeal = exportUpdateRequestDTO.getCompanySeal();
        this.purpose = exportUpdateRequestDTO.getPurpose();
        this.notes = exportUpdateRequestDTO.getNotes();
    }
}
