package kr.or.kmi.mis.api.docstorage.domain.request;

import lombok.Data;

import java.util.List;

@Data
public class DocStorageBulkUpdateRequestDTO {

    private String teamNm;
    private String manager;
    private String subManager;
    private String storageYear;
    private String createDate;
    private String transferDate;
    private String tsdNum;
    private String disposalDate;
    private String dpdNum;

    private List<Long> detailIds;

}
