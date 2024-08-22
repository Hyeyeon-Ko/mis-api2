package kr.or.kmi.mis.api.rental.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.or.kmi.mis.api.rental.model.request.RentalRequestDTO;
import kr.or.kmi.mis.api.rental.service.RentalService;
import kr.or.kmi.mis.cmm.model.response.ApiResponse;
import kr.or.kmi.mis.cmm.model.response.ResponseWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/rental")
@RequiredArgsConstructor
@Tag(name="RentalCRUD", description = "렌탈현황 관련 CRUD API")
public class RentalController {

    private final RentalService rentalService;

    @Operation(summary = "add Rental info", description = "렌탈현황 관련 정보 추가")
    @PostMapping("/")
    public ApiResponse<?> addRentalInfo(@RequestBody RentalRequestDTO rentalRequestDTO) {
        rentalService.addRentalInfo(rentalRequestDTO);
        return ResponseWrapper.success();
    }

    @Operation(summary = "get Rental info", description = "렌탈현황 관련 정보 조회, 렌탈 현황 정보 수정 시 사용")
    @GetMapping("/")
    public ApiResponse<?> getRentalInfo(@RequestParam("detailId") Long detailId) {
        return ResponseWrapper.success(rentalService.getRentalInfo(detailId));
    }

    @Operation(summary = "modify Rental info", description = "렌탈현황 관련 정보 수정")
    @PutMapping("/")
    public ApiResponse<?> updateRentalInfo(@RequestParam("detailId") Long detailId, @RequestBody RentalRequestDTO rentalRequestDTO) {
        rentalService.updateRentalInfo(detailId, rentalRequestDTO);
        return ResponseWrapper.success();
    }

    @Operation(summary = "delete Rental info", description = "렌탈현황 관련 정보 삭제")
    @DeleteMapping("/")
    public ApiResponse<?> deleteRentalInfo(@RequestParam("detailId") Long detailId) {
        rentalService.deleteRentalInfo(detailId);
        return ResponseWrapper.success();
    }

    @Operation(summary = "finish Rental info", description = "렌탈현황 관련 정보 업데이트 완료")
    @PutMapping("/finish")
    public ApiResponse<?> finishRentalInfo(@RequestBody List<Long> detailIds) {
        rentalService.finishRentalInfo(detailIds);
        return ResponseWrapper.success();
    }
}
