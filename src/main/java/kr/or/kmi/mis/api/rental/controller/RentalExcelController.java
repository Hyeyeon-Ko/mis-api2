package kr.or.kmi.mis.api.rental.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import kr.or.kmi.mis.api.rental.model.response.RentalExcelResponseDTO;
import kr.or.kmi.mis.api.rental.service.RentalExcelService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("api/rental")
@RequiredArgsConstructor
@Tag(name = "Rental Excel", description = "렌탈현황 엑셀 첨부 및 다운 API")
public class RentalExcelController {

    private final RentalExcelService rentalExcelService;

    @Operation(summary = "download excel", description = "렌탈현황 내역 엑셀 파일 다운로드")
    @PostMapping("/excel")
    public void downloadExcel(HttpServletResponse response, @RequestBody List<Long> detailIds) throws IOException {
        rentalExcelService.downloadExcel(response, detailIds);
    }

    @Operation(summary = "download excel data", description = "렌탈현황 내역 엑셀 데이터 다운로드")
    @PostMapping("/data")
    public void receiveData(@RequestBody List<RentalExcelResponseDTO> details) {
        rentalExcelService.saveRentalDetails(details);
    }

    @Operation(summary = "modify rental info with file", description = "렌탈현황 관련 정보 파일을 통한 수정")
    @PostMapping("/update")
    public void modifyRentalInfo(@RequestBody List<RentalExcelResponseDTO> details) {
        rentalExcelService.updateRentalDetails(details);
    }
}
