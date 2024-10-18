package kr.or.kmi.mis.api.toner.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.or.kmi.mis.api.toner.model.request.TonerExcelRequestDTO;
import kr.or.kmi.mis.api.toner.service.TonerExcelService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/toner")
@RequiredArgsConstructor
@Tag(name = "Toner Excel", description = "토너 엑셀 첨부 및 다운 API")
public class TonerExcelController {

    private final TonerExcelService tonerExcelService;

    @Operation(summary = "download excel", description = "토너 내역 엑셀 파일 다운로드")
    @PostMapping("/excel")
    public void tonerData(@RequestBody TonerExcelRequestDTO tonerExcelRequestDTO) {
        tonerExcelService.saveTonerDetails(tonerExcelRequestDTO);
    }
}
