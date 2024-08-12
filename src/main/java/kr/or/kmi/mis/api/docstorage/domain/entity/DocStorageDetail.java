package kr.or.kmi.mis.api.docstorage.domain.entity;

import jakarta.persistence.*;
import kr.or.kmi.mis.cmm.model.entity.BaseSystemFieldEntity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
@Table(name = "hrmtdsld")
public class DocStorageDetail extends BaseSystemFieldEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long detailId;

    @Column
    private Long draftId;

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

    @Column(length = 20)
    private String storageYear;

    @Column(length = 20)
    private String createDate;

    @Column(length = 20)
    private String transferDate;

    @Column(length = 50)
    private String tsdNum;         // 이관 기안번호

    @Column(length = 20)
    private String disposalDate;

    @Column(length = 50)
    private String dpdNum;         // 폐기 기안번호

    @Column(length = 20)
    private String registr;

    @Column(length = 20)
    private String lastupdtr;

    @Column(length = 20)
    private String lastupdtDt;


    @Builder
    public DocStorageDetail(String docId, String docNm, String teamNm, String manager, String subManager,
                            String storageYear, String createDate, String transferDate, String disposalDate, String registr) {
        this.docId = docId;
        this.docNm = docNm;
        this.teamNm = teamNm;
        this.manager = manager;
        this.subManager = subManager;
        this.storageYear = storageYear;
        this.createDate = createDate;
        this.transferDate = transferDate;
        this.disposalDate = disposalDate;
        this.registr = registr;
    }
}
