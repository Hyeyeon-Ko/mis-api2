package kr.or.kmi.mis.api.seal.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.or.kmi.mis.api.seal.model.response.ExportListResponseDTO;
import kr.or.kmi.mis.api.seal.model.response.ManagementListResponseDTO;
import kr.or.kmi.mis.api.seal.model.response.RegistrationListResponseDTO;
import kr.or.kmi.mis.api.seal.service.SealListService;
import kr.or.kmi.mis.cmm.model.response.ApiResponse;
import kr.or.kmi.mis.cmm.model.response.ResponseWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/seal")
@Tag(name = "Seal", description = "인장 관련 API")
public class SealListController {

    private final SealListService sealListService;

    @Operation(summary = "get seal management list", description = "인장관리대장 조회")
    @GetMapping("/managementList")
    public ApiResponse<List<ManagementListResponseDTO>> getSealManagementList(@RequestParam(required = false) LocalDate startDate,
                                                                              @RequestParam(required = false) LocalDate endDate) {
        return ResponseWrapper.success(sealListService.getSealManagementList(startDate, endDate));
    }

    @Operation(summary = "get seal export list", description = "인장반출대장 조회")
    @GetMapping("/exportList")
    public ApiResponse<List<ExportListResponseDTO>> getSealExportList(@RequestParam(required = false) LocalDate startDate,
                                                                  @RequestParam(required = false) LocalDate endDate) {
        return ResponseWrapper.success(sealListService.getSealExportList(startDate, endDate));
    }

    @Operation(summary = "get seal registration list", description = "인장등록대장 조회")
    @GetMapping("/registrationList")
    public ApiResponse<List<RegistrationListResponseDTO>> getSealRegistrationList(@RequestParam(required = false) LocalDate startDate,
                                                                              @RequestParam(required = false) LocalDate endDate) {
        return ResponseWrapper.success(sealListService.getSealRegistrationList(startDate, endDate));
    }

}
