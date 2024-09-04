package kr.or.kmi.mis.api.docstorage.domain.response;

import lombok.Builder;
import lombok.Data;

@Data
public class DocstorageExcelResponseDTO {

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
    private String dpdNum;
    private String deptCd;

    @Builder
    public DocstorageExcelResponseDTO(String teamNm, String docId, String location, String docNm, String manager,
                                      String subManager, String storageYear, String createDate, String transferDate,
                                      String tsdNum, String disposalDate, String dpdNum, String deptCd) {
        this.teamNm = teamNm;
        this.docId = docId;
        this.location = location;
        this.docNm = docNm;
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

}
