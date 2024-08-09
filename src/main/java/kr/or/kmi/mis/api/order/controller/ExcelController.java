package kr.or.kmi.mis.api.order.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import kr.or.kmi.mis.api.order.service.ExcelService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("api/bsc")
@RequiredArgsConstructor
@Tag(name = "Excel", description = "엑셀 다운로드 API")
public class ExcelController {

    private final ExcelService excelService;

    @Operation(summary = "download excel", description = "발주 요청내역 엑셀 파일 다운로드")
    @PostMapping("/order/excel")
    public void downloadExcel(HttpServletResponse response, @RequestBody List<Long> draftIds) throws IOException {
        excelService.downloadExcel(response, draftIds);
    }

    @Operation(summary = "download order excel", description = "발주 완료내역 엑셀 파일 다운로드")
    @PostMapping("/applyList/orderExcel")
    public void downloadOrderExcel(HttpServletResponse response, @RequestBody List<Long> draftIds) throws IOException {
        excelService.downloadOrderExcel(response, draftIds);
    }
}
