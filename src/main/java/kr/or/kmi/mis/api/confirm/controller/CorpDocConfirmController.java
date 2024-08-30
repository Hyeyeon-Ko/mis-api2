package kr.or.kmi.mis.api.confirm.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.or.kmi.mis.api.confirm.service.CorpDocConfirmService;
import kr.or.kmi.mis.cmm.model.response.ApiResponse;
import kr.or.kmi.mis.cmm.model.response.ResponseWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/corpDoc")
@Tag(name = "CorpDoc Confirm", description = "법인서류신청 승인 관리 API")
public class CorpDocConfirmController {

    public final CorpDocConfirmService corpDocConfirmService;

    @Operation(summary = "approve corpDoc apply", description = "법인서류 신청 승인")
    @PostMapping("/approve")
    public ApiResponse<?> approve(@RequestParam("draftId") Long draftId) {
        corpDocConfirmService.approve(draftId);
        return ResponseWrapper.success();
    }

    @Operation(summary = "reject corpDoc apply", description = "법인서류 신청 반려")
    @PostMapping("/reject")
    public ApiResponse<?> reject(@RequestParam("draftId") Long draftId, @RequestBody String rejectReason) {
        corpDocConfirmService.reject(draftId, rejectReason);
        return ResponseWrapper.success();
    }
}
