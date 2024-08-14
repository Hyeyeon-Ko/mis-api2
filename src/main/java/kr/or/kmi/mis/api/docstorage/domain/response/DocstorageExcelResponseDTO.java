package kr.or.kmi.mis.api.docstorage.domain.response;

import lombok.Builder;
import lombok.Data;

@Data
public class DocstorageExcelResponseDTO {

    // todo: 부서 코드 추가 필요!!!

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

    @Builder
    public DocstorageExcelResponseDTO(String teamNm, String docId, String location, String docNm, String manager,
                                      String subManager, String storageYear, String createDate, String transferDate,
                                      String tsdNum, String disposalDate, String dpdNum) {
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
    }

}
