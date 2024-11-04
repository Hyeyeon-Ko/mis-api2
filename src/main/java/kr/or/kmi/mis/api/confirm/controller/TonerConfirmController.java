package kr.or.kmi.mis.api.confirm.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.or.kmi.mis.api.confirm.service.TonerConfirmService;
import kr.or.kmi.mis.api.toner.model.request.TonerConfirmRequestDTO;
import kr.or.kmi.mis.cmm.model.response.ApiResponse;
import kr.or.kmi.mis.cmm.model.response.ResponseWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/toner/confirm")
@Tag(name = "Toner Confirm", description = "토너신청 승인/반려 관리 API")
public class TonerConfirmController {

    private final TonerConfirmService tonerConfirmService;

    @Operation(summary = "approve application", description = "토너신청 승인")
    @PostMapping
    public ApiResponse<?> approve(@RequestBody TonerConfirmRequestDTO tonerConfirmRequestDTO) {
        tonerConfirmService.approve(tonerConfirmRequestDTO);
        return ResponseWrapper.success();
    }

    @Operation(summary = "disapprove application", description = "토너신청 반려")
    @PostMapping("/return")
    public ApiResponse<?> disapprove(@RequestBody TonerConfirmRequestDTO tonerConfirmRequestDTO) {
        tonerConfirmService.disapprove(tonerConfirmRequestDTO);
        return ResponseWrapper.success();
    }
}
