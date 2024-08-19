package kr.or.kmi.mis.api.docstorage.domain.request;

import kr.or.kmi.mis.api.docstorage.domain.entity.DocStorageDetail;
import kr.or.kmi.mis.api.docstorage.domain.entity.DocStorageMaster;
import lombok.Builder;
import lombok.Getter;

import java.sql.Timestamp;
import java.util.List;

@Getter
@Builder
public class DocStorageApplyRequestDTO {

    private String instCd;
    private String deptCd;
    private String type;
    private List<Long> detailIds;

    public DocStorageMaster toMasterEntity(String drafter, String drafterId) {
        return DocStorageMaster.builder()
                .draftDate(new Timestamp(System.currentTimeMillis()))
                .drafter(drafter)
                .drafterId(drafterId)
                .instCd(instCd)
                .deptCd(deptCd)
                .type(type)
                .status("A")       // 신청완료
                .build();
    }
}
