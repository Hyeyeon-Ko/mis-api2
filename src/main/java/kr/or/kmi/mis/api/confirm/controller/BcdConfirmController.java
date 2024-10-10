package kr.or.kmi.mis.api.confirm.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import kr.or.kmi.mis.api.apply.model.request.ConfirmRequestDTO;
import kr.or.kmi.mis.api.bcd.model.response.BcdDetailResponseDTO;
import kr.or.kmi.mis.api.confirm.model.response.BcdHistoryResponseDTO;
import kr.or.kmi.mis.api.confirm.service.BcdConfirmService;
import kr.or.kmi.mis.cmm.model.request.PostPageRequest;
import kr.or.kmi.mis.cmm.model.request.PostSearchRequestDTO;
import kr.or.kmi.mis.cmm.model.response.ApiResponse;
import kr.or.kmi.mis.cmm.model.response.ResponseWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/bcd/applyList")
@Tag(name = "Bcd Confirm", description = "명함신청 승인/반려 관리 API")
public class BcdConfirmController {

    private final BcdConfirmService bcdConfirmService;

    @Operation(summary = "get apply list", description = "명함신청 목록 상세정보 조회")
    @GetMapping("/{draftId}")
    public ApiResponse<BcdDetailResponseDTO> getApplyList(@PathVariable String draftId) {
        return ResponseWrapper.success(bcdConfirmService.getBcdDetailInfo(draftId));
    }

    @Operation(summary = "approve application", description = "명함신청 승인")
    @PostMapping("/{draftId}")
    public ApiResponse<?> approve(@PathVariable String draftId, @RequestBody ConfirmRequestDTO confirmRequestDTO) {
        bcdConfirmService.approve(draftId, confirmRequestDTO);
        return ResponseWrapper.success();
    }

    @Operation(summary = "disapprove application", description = "명함신청 반려")
    @PostMapping("/return/{draftId}")
    public ApiResponse<?> disapprove(@PathVariable String draftId, @RequestBody ConfirmRequestDTO confirmRequestDTO) {
        bcdConfirmService.disapprove(draftId, confirmRequestDTO);
        return ResponseWrapper.success();
    }

    @Operation(summary = "get bcd application history by drafter", description = "기안자의 명함신청 이력 조회")
    @GetMapping("/history/{draftId}")
    public ApiResponse<List<BcdHistoryResponseDTO>> getApplicationHistory(@RequestParam(required = false) LocalDateTime startDate,
                                                                          @RequestParam(required = false) LocalDateTime endDate,
                                                                          @PathVariable String draftId) {
        return ResponseWrapper.success(bcdConfirmService.getBcdApplicationHistory(startDate, endDate, draftId));
    }

    @Operation(summary = "get bcd application history by drafter", description = "기안자의 명함신청 이력 조회")
    @GetMapping("/history2/{draftId}")
    public ApiResponse<Page<BcdHistoryResponseDTO>> getApplicationHistory2(@Valid PostSearchRequestDTO postSearchRequestDTO,
                                                                           PostPageRequest page,
                                                                           @PathVariable String draftId) {
        return ResponseWrapper.success(bcdConfirmService.getBcdApplicationHistory2(postSearchRequestDTO, page.of(), draftId));
    }
}
