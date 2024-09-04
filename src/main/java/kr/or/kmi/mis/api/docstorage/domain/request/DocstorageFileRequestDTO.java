package kr.or.kmi.mis.api.docstorage.domain.request;

import kr.or.kmi.mis.api.docstorage.domain.entity.DocStorageDetail;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class DocstorageFileRequestDTO {

    private List<DocStorageDetail> documents;
    private String teamCd;

}
