package kr.or.kmi.mis.api.docstorage.domain.entity;

import jakarta.persistence.*;
import kr.or.kmi.mis.api.docstorage.domain.request.DocStorageUpdateRequestDTO;
import kr.or.kmi.mis.api.docstorage.domain.response.DocstorageExcelResponseDTO;
import kr.or.kmi.mis.cmm.model.entity.BaseSystemFieldEntity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

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
    private String deptCd;


    @Builder
    public DocStorageDetail(Long detailId, String docId, String docNm, String location, String teamNm, String manager, String subManager,
                            String storageYear, String createDate, String transferDate, String tsdNum, String disposalDate, String dpdNum, String deptCd) {
        this.detailId = detailId;
        this.docId = docId;
        this.docNm = docNm;
        this.location = location;
        this.teamNm = teamNm;
        this.manager = manager;
        this.subManager = subManager;
        this.storageYear = storageYear;
        this.createDate = createDate;
        this.transferDate = transferDate;
        this.tsdNum = tsdNum;
        this.disposalDate = disposalDate;
        this.dpdNum = dpdNum;
        this.deptCd = deptCd;
    }

    public void update(DocStorageUpdateRequestDTO docStorageUpdateDTO) {
        this.docNm=docStorageUpdateDTO.getDocNm();
        this.location=docStorageUpdateDTO.getLocation();
        this.teamNm=docStorageUpdateDTO.getTeamNm();
        this.manager=docStorageUpdateDTO.getManager();
        this.subManager=docStorageUpdateDTO.getSubManager();
        this.storageYear=docStorageUpdateDTO.getStorageYear();
        this.createDate=docStorageUpdateDTO.getCreateDate();
        this.transferDate=docStorageUpdateDTO.getTransferDate();
        this.tsdNum=docStorageUpdateDTO.getTsdNum();
        this.disposalDate=docStorageUpdateDTO.getDisposalDate();
        this.dpdNum=docStorageUpdateDTO.getDpdNum();
    }
    
    public void updateExcelData(DocstorageExcelResponseDTO dto) {
        this.docNm=dto.getDocNm();
        this.location=dto.getLocation();
        System.out.println("this.location = " + this.location);
        this.teamNm=dto.getTeamNm();
        this.manager=dto.getManager();
        this.subManager=dto.getSubManager();
        this.storageYear=dto.getStorageYear();
        this.createDate=dto.getCreateDate();
        this.transferDate=dto.getTransferDate();
        this.tsdNum=dto.getTsdNum();
        this.disposalDate=dto.getDisposalDate();
        this.dpdNum=dto.getDpdNum();
    }

    public void updateDraftId(Long draftId) {
        this.draftId = draftId;
    }

    public void updateDeptCd(String deptCd) {
        this.deptCd = deptCd;
    }
}
