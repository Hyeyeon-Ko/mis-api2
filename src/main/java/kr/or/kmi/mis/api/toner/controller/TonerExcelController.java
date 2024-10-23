package kr.or.kmi.mis.api.toner.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import kr.or.kmi.mis.api.toner.service.TonerExcelService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/toner")
@RequiredArgsConstructor
@Tag(name = "Toner Excel", description = "토너 엑셀 첨부 및 다운 API")
public class TonerExcelController {

    private final TonerExcelService tonerExcelService;


//    @Operation(summary = "attach Price File", description = "토너 단가표 엑셀 파일 첨부")
//    @PostMapping("/price/file")
//    public void tonerData(@RequestBody TonerExcelRequestDTO tonerExcelRequestDTO) {
//        tonerExcelService.saveTonerDetails(tonerExcelRequestDTO);
//    }

//    @Operation(summary = "attach Manage File", description = "토너 관리표 엑셀 파일 첨부")
//    @PostMapping("/manage/file")
//    public void tonerData(@RequestBody TonerExcelRequestDTO tonerExcelRequestDTO) {
//        tonerExcelService.saveTonerDetails(tonerExcelRequestDTO);
//    }

    @Operation(summary = "get Toner Price Excel", description = "토너 단가표 엑셀 파일 다운로드")
    @PostMapping("/price/excel")
    public void downloadTonerPriceExcel(HttpServletResponse response, @RequestBody List<String> tonerNms) throws IOException {
        tonerExcelService.downloadPriceExcel(response, tonerNms);
    }

    @Operation(summary = "get Toner Manage Excel", description = "토너 관리표 엑셀 파일 다운로드")
    @PostMapping("/manage/excel")
    public void downloadTonerManageExcel(HttpServletResponse response, @RequestBody List<String> mngNums) throws IOException {
        tonerExcelService.downloadManageExcel(response, mngNums);
    }

    @Operation(summary = "get Toner Pending Excel", description = "기안 상신용 엑셀 파일 다운로드")
    @PostMapping("/pending/excel")
    public void downloadTonerPendingExcel(HttpServletResponse response, @RequestBody List<String> draftIds) throws IOException {
        tonerExcelService.downloadPendingExcel(response, draftIds);
    }

    @Operation(summary = "get Toner Order Excel", description = "발주용 엑셀 파일 다운로드")
    @PostMapping("/order/excel")
    public void downloadTonerOrderExcel(HttpServletResponse response, @RequestBody List<String> draftIds) throws IOException {
        tonerExcelService.downloadOrderExcel(response, draftIds);
    }
}
