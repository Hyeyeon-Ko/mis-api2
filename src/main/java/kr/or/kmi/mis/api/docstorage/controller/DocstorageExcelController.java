package kr.or.kmi.mis.api.docstorage.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
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
@Tag(name = "Excel", description = "엑셀 다운로드 API")
public class DocstorageExcelController {

    private final DocstorageExcelService docstorageExcelService;

    @Operation(summary = "download excel", description = "문서보관 내역 엑셀 파일 다운로드")
    @PostMapping("/excel")
    public void downloadExcel(HttpServletResponse response, @RequestBody List<Long> detailIds) throws IOException {
        docstorageExcelService.downloadExcel(response, detailIds);
    }

    @Operation(summary = "download excel data", description = "문서보관 내역 엑셀 데이터 다운로드")
    @PostMapping("/data")
    public void receiveData(@RequestBody List<DocstorageExcelResponseDTO> details) {
        docstorageExcelService.saveDocstorageDetails(details);
    }
}
