package kr.or.kmi.mis.api.docstorage.domain.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
public class DeptDocstorageListResponseDTO {

    private String deptCd;
    private List<DocstorageResponseDTO> docstorageResponseDTOList;
}
