package kr.or.kmi.mis.api.confirm.controller;

import kr.or.kmi.mis.api.bcd.model.response.BcdDetailResponseDTO;
import kr.or.kmi.mis.api.confirm.service.ConfirmService;
import kr.or.kmi.mis.cmm.response.ApiResponse;
import kr.or.kmi.mis.cmm.response.ResponseWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/bsc/applyList")
public class ConfirmController {

    private ConfirmService confirmService;

    /* 신청 목록 상세 조회 */
    @GetMapping("/{draftId}")
    public ApiResponse<BcdDetailResponseDTO> getApplyList(@PathVariable Long draftId) {
        return ResponseWrapper.success(confirmService.getBcdDetailInfo(draftId));
    }

    /* 승인 */
    @PostMapping("/{draftId}")
    public ApiResponse<?> approve(@PathVariable Long draftId) {
        confirmService.approve(draftId);
        return ResponseWrapper.success();
    }

    /* 반려 */
    @PostMapping("/return/{draftId}")
    public ApiResponse<?> disapprove(@PathVariable Long draftId, @RequestBody String rejectReason) {
        confirmService.disapprove(draftId, rejectReason);
        return ResponseWrapper.success();
    }
}
