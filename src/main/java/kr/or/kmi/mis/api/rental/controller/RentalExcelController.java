package kr.or.kmi.mis.api.rental.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import kr.or.kmi.mis.api.rental.model.response.RentalExcelResponseDTO;
import kr.or.kmi.mis.api.rental.service.RentalExcelService;
import kr.or.kmi.mis.cmm.model.response.ApiResponse;
import kr.or.kmi.mis.cmm.model.response.ResponseWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
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

    @Operation(summary = "download total excel", description = "전국 센터 렌탈현황 엑셀 파일 다운로드")
    @GetMapping("/totalExcel")
    public void downloadTotalExcel(HttpServletResponse response) throws IOException {
        byte[] excelData = rentalExcelService.generateTotalExcel();
        String encodedFileName = URLEncoder.encode("전국센터 렌탈제품 사용현황.xlsx", StandardCharsets.UTF_8.toString());

        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename*=UTF-8''" + encodedFileName);
        response.getOutputStream().write(excelData);
        response.getOutputStream().flush();
        response.getOutputStream().close();
    }

    @Operation(summary = "download excel data", description = "렌탈현황 내역 엑셀 데이터 다운로드")
    @PostMapping("/data")
    public ResponseEntity<ApiResponse<String>> receiveData(@RequestBody List<RentalExcelResponseDTO> details) {
        try {
            rentalExcelService.saveRentalDetails(details);
            return new ResponseEntity<>(ResponseWrapper.success("데이터가 성공적으로 저장되었습니다."), HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(ResponseWrapper.error(e.getMessage(), HttpStatus.BAD_REQUEST), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(ResponseWrapper.error("예상치 못한 오류가 발생했습니다: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(summary = "modify rental info with file", description = "렌탈현황 관련 정보 파일을 통한 수정")
    @PostMapping("/update")
    public void modifyRentalInfo(@RequestBody List<RentalExcelResponseDTO> details) {
        rentalExcelService.updateRentalDetails(details);
    }
}
