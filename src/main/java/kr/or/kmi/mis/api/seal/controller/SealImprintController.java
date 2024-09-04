package kr.or.kmi.mis.api.seal.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.or.kmi.mis.api.seal.model.request.ImprintRequestDTO;
import kr.or.kmi.mis.api.seal.model.request.ImprintUpdateRequestDTO;
import kr.or.kmi.mis.api.seal.model.response.SealImprintDetailResponseDTO;
import kr.or.kmi.mis.api.seal.service.SealImprintService;
import kr.or.kmi.mis.cmm.model.response.ApiResponse;
import kr.or.kmi.mis.cmm.model.response.ResponseWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/seal/imprint")
@RequiredArgsConstructor
@Tag(name = "Seal Imprint Apply", description = "인장 날인신청 관련 API")
public class SealImprintController {

    private final SealImprintService sealImprintService;

    @Operation(summary = "create imprint apply", description = "유저 > 날인신청 시 사용")
    @PostMapping
    public ApiResponse<?> createImprintApply(@RequestBody ImprintRequestDTO imprintRequestDTO) {

        sealImprintService.applyImprint(imprintRequestDTO);

        return ResponseWrapper.success();
    }

    @Operation(summary = "modify imprint apply", description = "유저 > 나의 신청내역 > 승인 대기 중인 날인신청 수정 시 사용")
    @PostMapping(value = "/update")
    public ApiResponse<?> updateImprintApply(@RequestParam Long draftId, @RequestBody ImprintUpdateRequestDTO imprintUpdateRequestDTO) {

        sealImprintService.updateImprint(draftId, imprintUpdateRequestDTO);

        return ResponseWrapper.success();
    }

    @Operation(summary = "cancel imprint apply", description = "유저 > 나의 신청내역 > 승인 대기 중인 날인신청 취소 시 사용")
    @PutMapping(value = "/{draftId}")
    public ApiResponse<?> cancelImprintApply(@PathVariable Long draftId) {

        sealImprintService.cancelImprint(draftId);

        return ResponseWrapper.success();
    }

    @Operation(summary = "get imprint detail", description = "날인신청 상세조회")
    @GetMapping(value = "/{draftId}")
    public ApiResponse<SealImprintDetailResponseDTO> getSealImprintDetail(@PathVariable Long draftId) {

        return ResponseWrapper.success(sealImprintService.getSealImprintDetail(draftId));
    }
}
