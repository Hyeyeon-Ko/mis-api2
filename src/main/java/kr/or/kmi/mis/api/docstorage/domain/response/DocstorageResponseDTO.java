package kr.or.kmi.mis.api.docstorage.domain.response;

import kr.or.kmi.mis.api.docstorage.domain.entity.DocStorageDetail;
import lombok.*;

@Data
public class DocstorageResponseDTO {

    private Long detailId;
    private String draftId;
    private String teamNm;
    private String docId;
    private String location;
    private String docNm;
    private String manager;
    private String subManager;
    private String storageYear;
    private String createDate;
    private String transferDate;
    private String tsdNum;
    private String disposalDate;
    private String dpdraftNum;
    private String type;
    private String status;

    @Builder
    public DocstorageResponseDTO(DocStorageDetail docStorageDetail, String type) {
        this.detailId = docStorageDetail.getDetailId();
        this.draftId = docStorageDetail.getDraftId();
        this.teamNm = docStorageDetail.getTeamNm();
        this.docId = docStorageDetail.getDocId();
        this.location = docStorageDetail.getLocation();
        this.docNm = docStorageDetail.getDocNm();
        this.manager = docStorageDetail.getManager();
        this.subManager = docStorageDetail.getSubManager();
        this.storageYear = docStorageDetail.getStorageYear();
        this.createDate = docStorageDetail.getCreateDate();
        this.transferDate = docStorageDetail.getTransferDate();
        this.tsdNum = docStorageDetail.getTsdNum();
        this.disposalDate = docStorageDetail.getDisposalDate();
        this.dpdraftNum = docStorageDetail.getDpdNum();
        this.type = type;
        this.status = docStorageDetail.getStatus();
    }
}
