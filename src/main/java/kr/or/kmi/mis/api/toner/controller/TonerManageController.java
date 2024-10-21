package kr.or.kmi.mis.api.toner.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.or.kmi.mis.api.toner.model.request.TonerInfoRequestDTO;
import kr.or.kmi.mis.api.toner.model.response.TonerExcelResponseDTO;
import kr.or.kmi.mis.api.toner.model.response.TonerTotalListResponseDTO;
import kr.or.kmi.mis.api.toner.service.TonerManageService;
import kr.or.kmi.mis.cmm.model.response.ApiResponse;
import kr.or.kmi.mis.cmm.model.response.ResponseWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/toner/manage")
@RequiredArgsConstructor
@Tag(name = "TonerManageList", description = "토너 관리표 호출 관련 API")
public class TonerManageController {

    private final TonerManageService tonerManageService;

    @Operation(summary = "get TonerManage List by Center", description = "센터별 토너 관리표 조회")
    @GetMapping("/list")
    public ApiResponse<List<TonerExcelResponseDTO>> getTonerList(String instCd) {
        return ResponseWrapper.success(tonerManageService.getTonerList(instCd));
    }

    @Operation(summary = "get TonerManage List", description = "전국 토너 관리표 조회")
    @GetMapping("/total")
    public ApiResponse<TonerTotalListResponseDTO> getTotalTonerList() {
        return ResponseWrapper.success(tonerManageService.getTotalTonerList());
    }

    @Operation(summary = "get Toner Info", description = "토너 관련 정보 조회, 토너 정보 수정 시 사용")
    @GetMapping("/{mngNum}")
    public ApiResponse<?> getTonerPrice(@PathVariable String mngNum) {
        return ResponseWrapper.success(tonerManageService.getTonerInfo(mngNum));
    }

    @Operation(summary = "add Toner Info", description = "토너 관련 정보 추가")
    @PostMapping
    public ApiResponse<?> addTonerInfo(TonerInfoRequestDTO tonerInfoRequestDTO, String userId, String instCd) {
        tonerManageService.addTonerInfo(tonerInfoRequestDTO, userId, instCd);
        return ResponseWrapper.success();
    }

    @Operation(summary = "modify Toner Info", description = "토너 관련 정보 수정")
    @PutMapping("/{mngNum}")
    public ApiResponse<?> updateTonerPrice(@PathVariable String mngNum, @RequestBody TonerInfoRequestDTO tonerInfoRequestDTO, String userId) {
        tonerManageService.updateTonerInfo(mngNum, tonerInfoRequestDTO, userId);
        return ResponseWrapper.success();
    }

    @Operation(summary = "delete Toner Info", description = "토너 관련 정보 삭제")
    @DeleteMapping("/{mngNum}")
    public ApiResponse<?> deleteTonerPrice(@RequestParam("tonerNm") String mngNum) {
        tonerManageService.deleteTonerInfo(mngNum);
        return ResponseWrapper.success();
    }
}
