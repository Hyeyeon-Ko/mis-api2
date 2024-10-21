package kr.or.kmi.mis.api.toner.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import kr.or.kmi.mis.api.toner.model.request.TonerAddRequestDTO;
import kr.or.kmi.mis.api.toner.model.response.TonerExcelResponseDTO;
import kr.or.kmi.mis.api.toner.model.response.TonerTotalListResponseDTO;
import kr.or.kmi.mis.api.toner.service.TonerManageService;
import kr.or.kmi.mis.cmm.model.response.ApiResponse;
import kr.or.kmi.mis.cmm.model.response.ResponseWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/toner/list")
@RequiredArgsConstructor
@Tag(name = "TonerManageList", description = "토너 관리표 호출 관련 API")
public class TonerManageController {

    private final TonerManageService tonerListService;

    @GetMapping("")
    public ApiResponse<List<TonerExcelResponseDTO>> getTonerList(String instCd) {
        return ResponseWrapper.success(tonerListService.getTonerList(instCd));
    }

    @GetMapping("/total")
    public ApiResponse<TonerTotalListResponseDTO> getTotalTonerList() {
        return ResponseWrapper.success(tonerListService.getTotalTonerList());
    }

    @PostMapping("")
    public ApiResponse<?> addToner(TonerAddRequestDTO tonerAddRequestDTO) {
        tonerListService.addToner(tonerAddRequestDTO);

        return ResponseWrapper.success();
    }

}
