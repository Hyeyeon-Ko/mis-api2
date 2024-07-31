package kr.or.kmi.mis.api.doc.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@ToString
@Entity
@Table(name = "hrmtdocd")
public class DocDetail {

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

    public void updateDocId(String docId) {
        this.docId = docId;
    }
}
