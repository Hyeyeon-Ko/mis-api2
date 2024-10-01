package kr.or.kmi.mis.api.confirm.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.or.kmi.mis.api.confirm.service.DocConfirmService;
import kr.or.kmi.mis.cmm.model.response.ApiResponse;
import kr.or.kmi.mis.cmm.model.response.ResponseWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/doc")
@Tag(name = "Doc Confirm", description = "문서수발신 승인 관리 API")
public class DocConfirmController {

    public final DocConfirmService docConfirmService;

    @Operation(summary = "confirm doc apply", description = "문서수발신 신청 승인")
    @PutMapping("/confirm")
    public ApiResponse<?> confirm(@RequestParam("draftId") String draftId, @RequestParam String userId) {
        docConfirmService.confirm(draftId, userId);
        return ResponseWrapper.success();
    }

    @Operation(summary = "delete doc apply", description = "승인 후, 사용자가 신청 취소 요청 시 삭제")
    @PutMapping("/delete")
    public ApiResponse<?> delete(@RequestParam("draftId") String draftId) {
        docConfirmService.delete(draftId);
        return ResponseWrapper.success();
    }
}
