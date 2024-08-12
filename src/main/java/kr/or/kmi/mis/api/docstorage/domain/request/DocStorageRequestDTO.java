package kr.or.kmi.mis.api.docstorage.domain.request;

import kr.or.kmi.mis.api.docstorage.domain.entity.DocStorageDetail;
import kr.or.kmi.mis.api.docstorage.domain.entity.DocStorageMaster;
import lombok.Getter;

import java.sql.Timestamp;

@Getter
public class DocStorageRequestDTO {

    private String drafter;
    private String drafterId;
    private String instCd;
    private String deptCd;
    private String type;
    private String docId;
    private String docNm;
    private String teamNm;     // 팀명
    private String manager;
    private String subManager;
    private String storageYear;
    private String createDate;
    private String transferDate;
    private String disposalDate;
    private String registr;

    public DocStorageMaster toMasterEntity() {
        return DocStorageMaster.builder()
                .draftDate(new Timestamp(System.currentTimeMillis()))
                .drafterId(drafterId)
                .drafter(drafter)
                .instCd(instCd)
                .deptCd(deptCd)
                .type(type)
                .status("N")
                .build();
    }

    public DocStorageDetail toDetailEntity() {
        return DocStorageDetail.builder()
                .docId(docId)
                .docNm(docNm)
                .teamNm(teamNm)
                .manager(manager)
                .subManager(subManager)
                .storageYear(storageYear)
                .createDate(createDate)
                .transferDate(transferDate)
                .disposalDate(disposalDate)
                .registr(registr)
                .build();
    }
}
