package kr.or.kmi.mis.api.confirm.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.or.kmi.mis.api.seal.model.response.ExportDetailResponseDTO;
import kr.or.kmi.mis.api.seal.model.response.ImprintDetailResponseDTO;
import kr.or.kmi.mis.api.seal.model.response.SealHistoryResponseDTO;
import kr.or.kmi.mis.api.confirm.service.SealConfirmService;
import kr.or.kmi.mis.cmm.model.response.ApiResponse;
import kr.or.kmi.mis.cmm.model.response.ResponseWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/seal")
@Tag(name = "Seal Confirm", description = "인장신청 승인/반려 관리 API")
public class SealConfirmController {

    private final SealConfirmService sealConfirmService;

    @Operation(summary = "approve application", description = "인장신청 승인")
    @PostMapping("/confirm")
    public ApiResponse<?> approve(@RequestParam Long draftId) {
        sealConfirmService.approve(draftId);
        return ResponseWrapper.success();
    }

    @Operation(summary = "disapprove application", description = "인장신청 반려")
    @PostMapping("/return")
    public ApiResponse<?> disapprove(@RequestParam Long draftId, @RequestBody String rejectReason) {
        sealConfirmService.disapprove(draftId, rejectReason);
        return ResponseWrapper.success();
    }
}
