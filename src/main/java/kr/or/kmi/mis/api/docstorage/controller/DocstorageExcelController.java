package kr.or.kmi.mis.api.docstorage.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import kr.or.kmi.mis.api.docstorage.domain.request.DocStorageExcelApplyRequestDTO;
import kr.or.kmi.mis.api.docstorage.domain.response.DocstorageExcelResponseDTO;
import kr.or.kmi.mis.api.docstorage.service.DocstorageExcelService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("api/docstorage")
@RequiredArgsConstructor
@Tag(name = "Docstorage Excel", description = "문서보관 엑셀 첨부 및 다운 API")
public class DocstorageExcelController {

    private final DocstorageExcelService docstorageExcelService;

    @Operation(summary = "download excel", description = "문서보관 내역 엑셀 파일 다운로드")
    @PostMapping("/excel")
    public void downloadExcel(HttpServletResponse response, @RequestBody List<Long> detailIds) throws IOException {
        docstorageExcelService.downloadExcel(response, detailIds);
    }

    @Operation(summary = "download excel data", description = "문서보관 내역 엑셀 데이터 다운로드")
    @PostMapping("/data")
    public void receiveData(@RequestBody List<DocstorageExcelResponseDTO> details, DocStorageExcelApplyRequestDTO docStorageExcelApplyRequestDTO) {
        docstorageExcelService.saveDocstorageDetails(details, docStorageExcelApplyRequestDTO);
    }

    @Operation(summary = "modify docStorage info with file", description = "문서보관 관련 정보 파일을 통한 수정")
    @PostMapping("/update")
    public void modifyDocStorageInfo(@RequestBody List<DocstorageExcelResponseDTO> details) {
        docstorageExcelService.updateDocstorageDetails(details);
    }

}
