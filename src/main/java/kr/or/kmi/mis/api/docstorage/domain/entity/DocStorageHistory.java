package kr.or.kmi.mis.api.docstorage.domain.entity;

import jakarta.persistence.*;
import kr.or.kmi.mis.cmm.model.entity.BaseSystemFieldEntity;
import lombok.*;

import java.sql.Timestamp;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@ToString
@Entity
@Table(name = "hrmtdsld_hist")
public class DocStorageHistory extends BaseSystemFieldEntity {

    @Id
    @Column(name = "draft_id")
    private Long draftId;

    @Column(length = 50)
    private String draftNum;

    @Column(length = 50)
    private String docId;

    @Column(length =1000)
    private String docNm;

    @Column(length =20)
    private String location;

    @Column(length = 20)
    private String teamNm;

    @Column(length =20)
    private String manager;

    @Column(length =20)
    private String subManager;

    @Column
    private int storageYear;

    @Column
    private int creationYear;

    @Column
    private Timestamp transferDate;

    @Column
    private Timestamp disposalDate;

    @Builder
    public DocStorageHistory(Long draftId, String draftNum, String docId, String docNm, String location, String teamNm,
                            String manager, String subManager, int storageYear, int creationYear, Timestamp transferDate) {
        this.draftId = draftId;
        this.draftNum = draftNum;
        this.docId = docId;
        this.docNm = docNm;
        this.location = location;
        this.teamNm = teamNm;
        this.manager = manager;
        this.subManager = subManager;
        this.storageYear = storageYear;
        this.creationYear = creationYear;
        this.transferDate = transferDate;
        this.disposalDate = transferDate;
    }
}
