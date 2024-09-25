package kr.or.kmi.mis.api.confirm.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.or.kmi.mis.api.bcd.model.response.BcdDetailResponseDTO;
import kr.or.kmi.mis.api.confirm.model.response.BcdHistoryResponseDTO;
import kr.or.kmi.mis.api.confirm.service.BcdConfirmService;
import kr.or.kmi.mis.cmm.model.response.ApiResponse;
import kr.or.kmi.mis.cmm.model.response.ResponseWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/bcd/applyList")
@Tag(name = "Bcd Confirm", description = "명함신청 승인/반려 관리 API")
public class BcdConfirmController {

    private final BcdConfirmService bcdConfirmService;

    @Operation(summary = "get apply list", description = "명함신청 목록 상세정보 조회")
    @GetMapping("/{draftId}")
    public ApiResponse<BcdDetailResponseDTO> getApplyList(@PathVariable Long draftId) {
        return ResponseWrapper.success(bcdConfirmService.getBcdDetailInfo(draftId));
    }

    @Operation(summary = "approve application", description = "명함신청 승인")
    @PostMapping("/{draftId}")
    public ApiResponse<?> approve(@PathVariable Long draftId, @RequestBody String userId) {
        bcdConfirmService.approve(draftId, userId);
        return ResponseWrapper.success();
    }

    @Operation(summary = "disapprove application", description = "명함신청 반려")
    @PostMapping("/return/{draftId}")
    public ApiResponse<?> disapprove(@PathVariable Long draftId, @RequestBody String rejectReason, @RequestBody String userId) {
        bcdConfirmService.disapprove(draftId, rejectReason, userId);
        return ResponseWrapper.success();
    }

    @Operation(summary = "get bcd application history by drafter", description = "기안자의 명함신청 이력 조회")
    @GetMapping("/history/{draftId}")
    public ApiResponse<List<BcdHistoryResponseDTO>> getApplicationHistory(@RequestParam(required = false) LocalDate startDate,
                                                                          @RequestParam(required = false) LocalDate endDate,
                                                                          @PathVariable Long draftId) {
        return ResponseWrapper.success(bcdConfirmService.getBcdApplicationHistory(startDate, endDate, draftId));
    }
}
