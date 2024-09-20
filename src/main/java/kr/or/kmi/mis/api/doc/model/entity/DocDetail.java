package kr.or.kmi.mis.api.doc.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import kr.or.kmi.mis.api.doc.model.request.SendDocRequestDTO;
import kr.or.kmi.mis.api.doc.model.request.DocUpdateRequestDTO;
import kr.or.kmi.mis.cmm.model.entity.BaseSystemFieldEntity;
import lombok.*;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@ToString
@Entity
@Table(name = "hrmtdocd")
public class DocDetail extends BaseSystemFieldEntity {

    @Id
    @Column(nullable = false)
    private Long draftId;

    @Column(length = 1)
    private String division;

    @Column(length = 20)
    private String receiver;

    @Column(length = 20)
    private String sender;

    @Column(length = 500)
    private String docTitle;

    @Column(length = 1000)
    private String purpose;

    @Column(length = 20)
    private String docId;

    @Column(length = 255)
    private String fileName;

    @Column(length = 255)
    private String filePath;

    public void updateDocId(String docId) {
        this.docId = docId;
    }

    @Builder
    public DocDetail(Long draftId, String division, String receiver, String sender, String docTitle,
                     String purpose, String docId, String fileName, String filePath) {
        this.draftId = draftId;
        this.division = division;
        this.receiver = receiver;
        this.sender = sender;
        this.docTitle = docTitle;
        this.purpose = purpose;
        this.docId = docId;
        this.fileName = fileName;
        this.filePath = filePath;
    }

    public void update(SendDocRequestDTO requestDTO, String fileName, String filePath) {
        this.division = requestDTO.getDivision();
        this.receiver = requestDTO.getReceiver();
        this.sender = requestDTO.getSender();
        this.docTitle = requestDTO.getDocTitle();
        this.purpose = requestDTO.getPurpose();
        this.fileName = fileName;
        this.filePath = filePath;
    }

    public void updateFile(DocUpdateRequestDTO docUpdateRequestDTO, String fileName, String filePath) {
        this.division = docUpdateRequestDTO.getDivision();
        this.receiver = docUpdateRequestDTO.getReceiver();
        this.sender = docUpdateRequestDTO.getSender();
        this.docTitle = docUpdateRequestDTO.getDocTitle();
        this.purpose = docUpdateRequestDTO.getPurpose();
        this.fileName = fileName;
        this.filePath = filePath;
    }
}
