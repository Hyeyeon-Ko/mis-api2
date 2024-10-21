package kr.or.kmi.mis.api.toner.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.or.kmi.mis.api.toner.model.request.TonerPriceRequestDTO;
import kr.or.kmi.mis.api.toner.model.response.TonerPriceResponseDTO;
import kr.or.kmi.mis.api.toner.service.TonerPriceService;
import kr.or.kmi.mis.cmm.model.response.ApiResponse;
import kr.or.kmi.mis.cmm.model.response.ResponseWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/toner/price")
@RequiredArgsConstructor
@Tag(name = "TonerPrice", description = "토너 단가표 호출 관련 API")
public class TonerPriceController {

    private final TonerPriceService tonerPriceService;

    @Operation(summary = "get TonerPrice List", description = "토너 단가표 조회")
    @GetMapping("/list")
    public ApiResponse<List<TonerPriceResponseDTO>> getTonerPriceList() {
        return ResponseWrapper.success(tonerPriceService.getTonerPriceList());
    }

    @Operation(summary = "get Toner info", description = "토너 관련 정보 조회, 토너 정보 수정 시 사용")
    @GetMapping("/{tonerNm}")
    public ApiResponse<?> getTonerInfo(@PathVariable String tonerNm) {
        return ResponseWrapper.success(tonerPriceService.getTonerPriceInfo(tonerNm));
    }

    @Operation(summary = "add Toner info", description = "토너 관련 정보 추가")
    @PostMapping
    public ApiResponse<?> addTonerInfo(@RequestBody TonerPriceRequestDTO tonerPriceRequestDTO, String userId) {
        tonerPriceService.addTonerPriceInfo(tonerPriceRequestDTO, userId);
        return ResponseWrapper.success();
    }

    @Operation(summary = "modify Toner info", description = "토너 관련 정보 수정")
    @PutMapping("/{tonerNm}")
    public ApiResponse<?> updateTonerInfo(@PathVariable String tonerNm, @RequestBody TonerPriceRequestDTO tonerPriceRequestDTO, String userId) {
        tonerPriceService.updateTonerPriceInfo(tonerNm, tonerPriceRequestDTO, userId);
        return ResponseWrapper.success();
    }

    @Operation(summary = "delete Toner info", description = "토너 관련 정보 삭제")
    @DeleteMapping("/{tonerNm}")
    public ApiResponse<?> deleteTonerInfo(@RequestParam("tonerNm") String tonerNm) {
        tonerPriceService.deleteTonerPriceInfo(tonerNm);
        return ResponseWrapper.success();
    }

}
