package kr.or.kmi.mis.api.seal.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.or.kmi.mis.api.seal.model.response.ExportListResponseDTO;
import kr.or.kmi.mis.api.seal.model.response.ManagementListResponseDTO;
import kr.or.kmi.mis.api.seal.model.response.RegistrationListResponseDTO;
import kr.or.kmi.mis.api.seal.model.response.TotalRegistrationListResponseDTO;
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
@Tag(name = "Seal List", description = "인장 관련 API")
public class SealListController {

    private final SealListService sealListService;

    @Operation(summary = "get seal management list", description = "센터별 인장관리대장 조회")
    @GetMapping("/managementList")
    public ApiResponse<List<ManagementListResponseDTO>> getSealManagementList(@RequestParam(required = false) String searchType,
                                                                              @RequestParam(required = false) String keyword,
                                                                              @RequestParam String instCd) {
        return ResponseWrapper.success(sealListService.getSealManagementList(searchType, keyword, instCd));
    }

    @Operation(summary = "get seal export list", description = "센터별 인장반출대장 조회")
    @GetMapping("/exportList")
    public ApiResponse<List<ExportListResponseDTO>> getSealExportList(@RequestParam(required = false) String searchType,
                                                                      @RequestParam(required = false) String keyword,
                                                                      @RequestParam String instCd) {
        return ResponseWrapper.success(sealListService.getSealExportList(searchType, keyword, instCd));
    }

    @Operation(summary = "get seal registration list", description = "센터별 인장등록대장 조회")
    @GetMapping("/registrationList")
    public ApiResponse<List<RegistrationListResponseDTO>> getSealRegistrationList(@RequestParam String instCd) {
        return ResponseWrapper.success(sealListService.getSealRegistrationList(instCd));
    }

    @Operation(summary = "get total seal registration list", description = "전국 인장등록대장 조회")
    @GetMapping("/totalRegistrationList")
    public ApiResponse<List<TotalRegistrationListResponseDTO>> getTotalSealRegistrationList() {

        return ResponseWrapper.success(sealListService.getTotalSealRegistrationList());
    }
}
