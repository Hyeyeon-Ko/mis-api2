package kr.or.kmi.mis.api.seal.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.or.kmi.mis.api.seal.model.request.ExportRequestDTO;
import kr.or.kmi.mis.api.seal.model.request.ExportUpdateRequestDTO;
import kr.or.kmi.mis.api.seal.service.SealExportService;
import kr.or.kmi.mis.cmm.model.response.ApiResponse;
import kr.or.kmi.mis.cmm.model.response.ResponseWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/seal/export")
@RequiredArgsConstructor
@Tag(name = "Seal Export Apply", description = "인장 반출신청 관련 API")
public class SealExportController {

    private final SealExportService sealExportService;

    @Operation(summary = "create export apply", description = "유저 > 반출신청 시 사용")
    @PostMapping
    public ApiResponse<?> createExportApply(@RequestPart ExportRequestDTO exportRequestDTO,
                                            @RequestPart(value = "file", required = false) MultipartFile file) throws IOException {

        sealExportService.applyExport(exportRequestDTO, file);

        return ResponseWrapper.success();
    }

    @Operation(summary = "modify export apply", description = "유저 > 나의 신청내역 > 승인 대기 중인 반출신청 수정 시 사용")
    @PostMapping(value = "/update")
    public ApiResponse<?> updateExportApply(@RequestParam Long draftId, @RequestPart ExportUpdateRequestDTO exportUpdateRequestDTO,
                                            @RequestPart(value = "file", required = false) MultipartFile file, boolean isFileDeleted) throws IOException {

        sealExportService.updateExport(draftId, exportUpdateRequestDTO, file, isFileDeleted);

        return ResponseWrapper.success();
    }

    @Operation(summary = "cancel export apply", description = "유저 > 나의 신청내역 > 승인 대기 중인 반출신청 취소 시 사용")
    @PutMapping(value = "/{draftId}")
    public ApiResponse<?> cancelExportApply(@PathVariable Long draftId) {

        sealExportService.cancelExport(draftId);

        return ResponseWrapper.success();
    }

}
