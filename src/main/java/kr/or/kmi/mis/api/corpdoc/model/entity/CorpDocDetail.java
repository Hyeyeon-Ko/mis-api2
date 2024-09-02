package kr.or.kmi.mis.api.corpdoc.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import kr.or.kmi.mis.api.corpdoc.model.request.CorpDocUpdateRequestDTO;
import kr.or.kmi.mis.cmm.model.entity.BaseSystemFieldEntity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
@Table(name = "hrmtcorpd")
public class CorpDocDetail extends BaseSystemFieldEntity {

    @Id
    @Column(nullable = false)
    private Long draftId;

    @Column(length = 50)
    private String submission;

    @Column(length = 1000)
    private String purpose;

    @Column(length = 20)
    private String useDate;     // 서류 사용일자

    @Column(length = 20)
    private String issueDate;   // 서류 입고/발급일자

    @Column(length = 255)
    private String fileName;

    @Column(length = 255)
    private String filePath;

    @Column
    private int certCorpseal;

    @Column
    private int totalCorpseal;

    @Column
    private int certCoregister;

    @Column
    private int totalCoregister;

    @Column
    private int certUsesignet;

    @Column
    private int warrant;

    @Column(length = 1)
    private String type;

    @Column(length = 1000)
    private String notes;

    @Builder
    public CorpDocDetail(Long draftId, String submission, String purpose, String useDate, String fileName, String filePath,
                         int certCorpseal, int certCoregister, int certUsesignet, int warrant, String type, String notes, String issueDate) {
        this.draftId = draftId;
        this.submission = submission;
        this.purpose = purpose;
        this.useDate = useDate;
        this.fileName = fileName;
        this.filePath = filePath;
        this.certCorpseal = certCorpseal;
        this.certCoregister = certCoregister;
        this.certUsesignet = certUsesignet;
        this.warrant = warrant;
        this.type = type;
        this.notes = notes;
        this.issueDate = issueDate;
    }

    public void update(CorpDocUpdateRequestDTO corpDocUpdateRequestDTO, String newFileName, String newFilePath) {
        this.submission = corpDocUpdateRequestDTO.getSubmission();
        this.purpose = corpDocUpdateRequestDTO.getPurpose();
        this.useDate = corpDocUpdateRequestDTO.getUseDate();
        this.fileName = newFileName;
        this.filePath = newFilePath;
        this.certCorpseal = corpDocUpdateRequestDTO.getCertCorpseal();
        this.certCoregister = corpDocUpdateRequestDTO.getCertCoregister();
        this.certUsesignet = corpDocUpdateRequestDTO.getCertUsesignet();
        this.warrant = corpDocUpdateRequestDTO.getWarrant();
        this.type = corpDocUpdateRequestDTO.getType();
        this.notes = corpDocUpdateRequestDTO.getNotes();
    }

    public void updateDateAndTotal(int totalCorpseal, int totalCoregister) {
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        this.issueDate = new SimpleDateFormat("yyyy-MM-dd").format(timestamp);
        this.totalCorpseal = totalCorpseal;
        this.totalCoregister = totalCoregister;
    }
}
