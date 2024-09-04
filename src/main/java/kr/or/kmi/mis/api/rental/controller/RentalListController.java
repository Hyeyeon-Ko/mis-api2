package kr.or.kmi.mis.api.rental.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import kr.or.kmi.mis.api.docstorage.domain.response.DocstorageResponseDTO;
import kr.or.kmi.mis.api.docstorage.domain.response.DocstorageTotalListResponseDTO;
import kr.or.kmi.mis.api.rental.model.response.RentalResponseDTO;
import kr.or.kmi.mis.api.rental.model.response.RentalTotalListResponseDTO;
import kr.or.kmi.mis.api.rental.service.RentalListService;
import kr.or.kmi.mis.cmm.model.response.ApiResponse;
import kr.or.kmi.mis.cmm.model.response.ResponseWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/rentalList")
@RequiredArgsConstructor
@Tag(name = "RentalList", description = "렌탈현황 호출 관련 API")
public class RentalListController {

    private final RentalListService rentalListService;

    @GetMapping("/center")
    public ApiResponse<List<RentalResponseDTO>> getCenterRentalList(String instCd) {
        return ResponseWrapper.success(rentalListService.getCenterRentalList(instCd));
    }

    @GetMapping("/total")
    public ApiResponse<RentalTotalListResponseDTO> getTotalRentalList() {
        return ResponseWrapper.success(rentalListService.getTotalRentalList());
    }
}
