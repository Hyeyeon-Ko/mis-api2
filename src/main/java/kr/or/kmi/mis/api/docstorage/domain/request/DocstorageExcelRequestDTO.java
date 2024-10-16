package kr.or.kmi.mis.api.docstorage.domain.request;

import kr.or.kmi.mis.api.docstorage.domain.response.DocstorageExcelResponseDTO;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class DocstorageExcelRequestDTO {

    private List<DocstorageExcelResponseDTO> details;
    private DocStorageExcelApplyRequestDTO docStorageExcelApplyRequestDTO;

}
