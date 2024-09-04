package kr.or.kmi.mis.api.docstorage.domain.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
public class DocstorageCenterListResponseDTO {

    private List<DeptResponseDTO> deptResponses;
    private List<DeptDocstorageListResponseDTO> deptDocstorageResponses;

    public static DocstorageCenterListResponseDTO of(List<DeptResponseDTO> deptResponses, List<DeptDocstorageListResponseDTO> deptDocstorageResponses) {
        return DocstorageCenterListResponseDTO.builder()
                .deptResponses(deptResponses)
                .deptDocstorageResponses(deptDocstorageResponses)
                .build();
    }

}
