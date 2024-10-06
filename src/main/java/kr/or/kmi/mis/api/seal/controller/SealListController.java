package kr.or.kmi.mis.api.seal.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import kr.or.kmi.mis.api.apply.model.request.ApplyRequestDTO;
import kr.or.kmi.mis.api.seal.model.response.ExportListResponseDTO;
import kr.or.kmi.mis.api.seal.model.response.ManagementListResponseDTO;
import kr.or.kmi.mis.api.seal.model.response.RegistrationListResponseDTO;
import kr.or.kmi.mis.api.seal.model.response.TotalRegistrationListResponseDTO;
import kr.or.kmi.mis.api.seal.service.SealListService;
import kr.or.kmi.mis.cmm.model.request.PostPageRequest;
import kr.or.kmi.mis.cmm.model.request.PostSearchRequestDTO;
import kr.or.kmi.mis.cmm.model.response.ApiResponse;
import kr.or.kmi.mis.cmm.model.response.ResponseWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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

    @Operation(summary = "get seal management list", description = "센터별 인장관리대장 조회")
    @GetMapping("/managementList2")
    public ApiResponse<Page<ManagementListResponseDTO>> getSealManagementList2(@Valid ApplyRequestDTO applyRequestDTO,
                                                                               @Valid PostSearchRequestDTO postSearchRequestDTO,
                                                                               PostPageRequest page) {
        return ResponseWrapper.success(sealListService.getSealManagementList2(applyRequestDTO, postSearchRequestDTO, page.of()));
    }

    @Operation(summary = "get seal export list", description = "센터별 인장반출대장 조회")
    @GetMapping("/exportList")
    public ApiResponse<List<ExportListResponseDTO>> getSealExportList(@RequestParam(required = false) String searchType,
                                                                      @RequestParam(required = false) String keyword,
                                                                      @RequestParam String instCd) {
        return ResponseWrapper.success(sealListService.getSealExportList(searchType, keyword, instCd));

    }

    @Operation(summary = "get seal export list", description = "센터별 인장반출대장 조회")
    @GetMapping("/exportList2")
    public ApiResponse<Page<ExportListResponseDTO>> getSealExportList2(@Valid ApplyRequestDTO applyRequestDTO,
                                                                       @Valid PostSearchRequestDTO postSearchRequestDTO,
                                                                       PostPageRequest page) {
        return ResponseWrapper.success(sealListService.getSealExportList2(applyRequestDTO, postSearchRequestDTO, page.of()));
    }

    @Operation(summary = "get seal registration list", description = "센터별 인장등록대장 조회")
    @GetMapping("/registrationList")
    public ApiResponse<List<RegistrationListResponseDTO>> getSealRegistrationList(@RequestParam String instCd) {
        return ResponseWrapper.success(sealListService.getSealRegistrationList(instCd));
    }

    @Operation(summary = "get seal registration list", description = "센터별 인장등록대장 조회")
    @GetMapping("/registrationList2")
    public ApiResponse<Page<RegistrationListResponseDTO>> getSealRegistrationList2(@Valid ApplyRequestDTO applyRequestDTO,
                                                                                   PostPageRequest page) {
        return ResponseWrapper.success(sealListService.getSealRegistrationList2(applyRequestDTO, page.of()));
    }

    @Operation(summary = "get total seal registration list", description = "전국 인장등록대장 조회")
    @GetMapping("/totalRegistrationList")
    public ApiResponse<List<TotalRegistrationListResponseDTO>> getTotalSealRegistrationList() {

        return ResponseWrapper.success(sealListService.getTotalSealRegistrationList());
    }

    @Operation(summary = "get total seal registration list", description = "전국 인장등록대장 조회")
    @GetMapping("/totalRegistrationList2")
    public ApiResponse<Page<TotalRegistrationListResponseDTO>> getTotalSealRegistrationList2(PostPageRequest page) {

        return ResponseWrapper.success(sealListService.getTotalSealRegistrationList2(page.of()));
    }
}
