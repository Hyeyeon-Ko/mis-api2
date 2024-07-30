package kr.or.kmi.mis.api.confirm.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.or.kmi.mis.api.bcd.model.response.BcdDetailResponseDTO;
import kr.or.kmi.mis.api.confirm.model.response.BcdHistoryResponseDTO;
import kr.or.kmi.mis.api.confirm.service.ConfirmService;
import kr.or.kmi.mis.cmm.model.response.ApiResponse;
import kr.or.kmi.mis.cmm.model.response.ResponseWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/bcd/applyList")
@Tag(name = "Confirm", description = "승인/반려 관리 API")
public class ConfirmController {

    private final ConfirmService confirmService;

    // todo: 동일한 api 있으므로 추후 연동 path 변경
    @Operation(summary = "get apply list", description = "신청 목록 상세정보 조회")
    @GetMapping("/{draftId}")
    public ApiResponse<BcdDetailResponseDTO> getApplyList(@PathVariable Long draftId) {
        return ResponseWrapper.success(confirmService.getBcdDetailInfo(draftId));
    }

    @Operation(summary = "approve application", description = "신청 승인")
    @PostMapping("/{draftId}")
    public ApiResponse<?> approve(@PathVariable Long draftId) {
        confirmService.approve(draftId);
        return ResponseWrapper.success();
    }

    @Operation(summary = "disapprove application", description = "신청 반려")
    @PostMapping("/return/{draftId}")
    public ApiResponse<?> disapprove(@PathVariable Long draftId, @RequestBody String rejectReason) {
        confirmService.disapprove(draftId, rejectReason);
        return ResponseWrapper.success();
    }

    @Operation(summary = "get application history by drafter", description = "기안자의 신청 이력 조회")
    @GetMapping("/history/{draftId}")
    public ApiResponse<List<BcdHistoryResponseDTO>> getApplicationHistory(@PathVariable Long draftId) {
        return ResponseWrapper.success(confirmService.getApplicationHistory(draftId));
    }
}
