package kr.or.kmi.mis.api.docstorage.domain.request;

import kr.or.kmi.mis.api.docstorage.domain.entity.DocStorageMaster;
import lombok.Data;

import java.sql.Timestamp;

@Data
public class DocStorageExcelApplyRequestDTO {

    private String instCd;
    private String deptCd;
    private String drafter;
    private String drafterId;

    public DocStorageMaster toMasterEntity(DocStorageExcelApplyRequestDTO docStorageExcelApplyRequestDTO) {
        return DocStorageMaster.builder()
                .draftDate(new Timestamp(System.currentTimeMillis()).toLocalDateTime())
                .drafter(docStorageExcelApplyRequestDTO.getDrafter())
                .drafterId(docStorageExcelApplyRequestDTO.getDrafterId())
                .instCd(docStorageExcelApplyRequestDTO.getInstCd())
                .deptCd(docStorageExcelApplyRequestDTO.getDeptCd())
                .status("A")       // 신청완료
                .build();
    }
}
