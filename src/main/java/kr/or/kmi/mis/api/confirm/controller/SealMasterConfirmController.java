package kr.or.kmi.mis.api.confirm.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.or.kmi.mis.api.apply.model.request.ConfirmRequestDTO;
import kr.or.kmi.mis.api.confirm.service.SealMasterConfirmService;
import kr.or.kmi.mis.cmm.model.response.ApiResponse;
import kr.or.kmi.mis.cmm.model.response.ResponseWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/seal")
@Tag(name = "Seal Confirm", description = "인장신청 승인/반려 관리 API")
public class SealMasterConfirmController {

    private final SealMasterConfirmService sealMasterConfirmService;

    @Operation(summary = "approve application", description = "인장신청 승인")
    @PostMapping("/{draftId}")
    public ApiResponse<?> approve(@PathVariable String draftId, @RequestBody ConfirmRequestDTO confirmRequestDTO) {
        sealMasterConfirmService.approve(draftId, confirmRequestDTO);
        return ResponseWrapper.success();
    }

    @Operation(summary = "disapprove application", description = "인장신청 반려")
    @PostMapping("/return/{draftId}")
    public ApiResponse<?> disapprove(@PathVariable String draftId, @RequestBody ConfirmRequestDTO confirmRequestDTO) {
        sealMasterConfirmService.disapprove(draftId, confirmRequestDTO);
        return ResponseWrapper.success();
    }
}
