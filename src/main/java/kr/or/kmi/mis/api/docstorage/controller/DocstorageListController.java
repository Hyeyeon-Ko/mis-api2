package kr.or.kmi.mis.api.docstorage.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import kr.or.kmi.mis.api.docstorage.domain.response.DeptResponseDTO;
import kr.or.kmi.mis.api.docstorage.domain.response.DocstorageResponseDTO;
import kr.or.kmi.mis.api.docstorage.domain.response.DocstorageTotalListResponseDTO;
import kr.or.kmi.mis.api.docstorage.service.DocstorageListService;
import kr.or.kmi.mis.cmm.model.response.ApiResponse;
import kr.or.kmi.mis.cmm.model.response.ResponseWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/docstorageList")
@RequiredArgsConstructor
@Tag(name = "DocstorageList", description = "문서보관 호출 관련 API")
public class DocstorageListController {

    private final DocstorageListService docstorageListService;

    @GetMapping("/dept")
    public ApiResponse<List<DocstorageResponseDTO>> getDocstorageDeptList(String deptCd) {
        return ResponseWrapper.success(docstorageListService.getDocstorageDeptList(deptCd));
    }

    @GetMapping("/pending")
    public ApiResponse<List<DocstorageResponseDTO>> getDocstoragePendingList(String instCd) {
        return ResponseWrapper.success(docstorageListService.getDocstoragePendingList(instCd));
    }

    @GetMapping("/center")
    public ApiResponse<List<DocstorageResponseDTO>> getDocstorageCenterList(String deptCd) {
        return ResponseWrapper.success(docstorageListService.getDocstorageCenterList(deptCd));
    }

    @GetMapping("/totalDept")
    public ApiResponse<List<DocstorageResponseDTO>> getTotalCenterDocstorageList(String instCd) {
        return ResponseWrapper.success(docstorageListService.getTotalCenterDocstorageList(instCd));
    }

    @GetMapping("/total")
    public ApiResponse<DocstorageTotalListResponseDTO> getTotalDocstorageList() {
        return ResponseWrapper.success(docstorageListService.getTotalDocstorageList());
    }

    @GetMapping("/deptList")
    public ApiResponse<List<DeptResponseDTO>> getDeptListForCenter(String instCd) {
        return ResponseWrapper.success(docstorageListService.getDeptListForCenter(instCd));
    }
}
