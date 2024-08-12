package kr.or.kmi.mis.api.docstorage.domain.request;

import kr.or.kmi.mis.api.docstorage.domain.entity.DocStorageDetail;
import lombok.Getter;

@Getter
public class DocStorageUpdateRequestDTO {

    private String docId;
    private String docNm;
    private String location;
    private String teamNm;
    private String manager;
    private String subManager;
    private String storageYear;
    private String createDate;
    private String transferDate;
    private String tsdNum;
    private String disposalDate;
    private String dpdNum;

}
