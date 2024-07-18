package kr.or.kmi.mis.api.confirm.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.or.kmi.mis.api.bcd.model.response.BcdDetailResponseDTO;
import kr.or.kmi.mis.api.confirm.service.ConfirmService;
import kr.or.kmi.mis.cmm.response.ApiResponse;
import kr.or.kmi.mis.cmm.response.ResponseWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/bcd/applyList")
@Tag(name = "Confirm", description = "승인/반려 관리 API")
public class ConfirmController {

    private final ConfirmService confirmService;

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
}
