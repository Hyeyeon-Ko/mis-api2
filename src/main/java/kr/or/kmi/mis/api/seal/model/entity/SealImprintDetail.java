package kr.or.kmi.mis.api.seal.model.entity;

import jakarta.persistence.*;
import kr.or.kmi.mis.api.seal.model.request.ImprintUpdateRequestDTO;
import kr.or.kmi.mis.cmm.model.entity.BaseSystemFieldEntity;
import lombok.*;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@ToString
@Entity
@Table(name = "hrmtimpd")
public class SealImprintDetail extends BaseSystemFieldEntity {

    @Id
    @Column(name = "draft_id")
    private Long draftId;

    @Column(length = 50)
    private String submission;

    private String useDate;

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
    public SealImprintDetail(Long draftId, String submission, String useDate, String corporateSeal, String facsimileSeal,
                             String companySeal, String purpose, String notes) {
        this.draftId = draftId;
        this.submission = submission;
        this.useDate = useDate;
        this.corporateSeal = corporateSeal;
        this.facsimileSeal = facsimileSeal;
        this.companySeal = companySeal;
        this.purpose = purpose;
        this.notes = notes;
    }

    public void update(ImprintUpdateRequestDTO imprintUpdateRequestDTO) {
        this.submission = imprintUpdateRequestDTO.getSubmission();
        this.useDate = imprintUpdateRequestDTO.getUseDate();
        this.corporateSeal = imprintUpdateRequestDTO.getCorporateSeal();
        this.facsimileSeal = imprintUpdateRequestDTO.getFacsimileSeal();
        this.companySeal = imprintUpdateRequestDTO.getCompanySeal();
        this.purpose = imprintUpdateRequestDTO.getPurpose();
        this.notes = imprintUpdateRequestDTO.getNotes();
    }
}
