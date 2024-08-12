package kr.or.kmi.mis.api.docstorage.domain.request;

import kr.or.kmi.mis.api.docstorage.domain.entity.DocStorageDetail;
import lombok.Getter;

@Getter
public class DocStorageRequestDTO {

    private String docId;
    private String docNm;
    private String teamNm;     // 팀명
    private String manager;
    private String subManager;
    private String storageYear;
    private String createDate;
    private String disposalDate;

    public DocStorageDetail toDetailEntity() {
        return DocStorageDetail.builder()
                .docId(docId)
                .docNm(docNm)
                .teamNm(teamNm)
                .manager(manager)
                .subManager(subManager)
                .storageYear(storageYear)
                .createDate(createDate)
                .disposalDate(disposalDate)
                .build();
    }
}
