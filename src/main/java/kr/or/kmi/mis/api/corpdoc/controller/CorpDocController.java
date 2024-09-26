package kr.or.kmi.mis.api.corpdoc.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.or.kmi.mis.api.corpdoc.model.request.CorpDocRequestDTO;
import kr.or.kmi.mis.api.corpdoc.model.request.CorpDocUpdateRequestDTO;
import kr.or.kmi.mis.api.corpdoc.model.response.CorpDocDetailResponseDTO;
import kr.or.kmi.mis.api.corpdoc.service.CorpDocService;
import kr.or.kmi.mis.cmm.model.response.ApiResponse;
import kr.or.kmi.mis.cmm.model.response.ResponseWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/corpDoc")
@RequiredArgsConstructor
@Tag(name="CorpDocCRUD", description = "법인서류 관련 CRUD API")
public class CorpDocController {

    private final CorpDocService corpDocService;

    @Operation(summary = "create corpDoc apply", description = "법인서류 신청")
    @PostMapping("/")
    public ApiResponse<?> createCorpDocApply(
            @RequestPart("corpDocRequest") CorpDocRequestDTO corpDocRequestDTO,
            @RequestPart(value = "file", required = false) MultipartFile file) throws Exception {

        corpDocService.createCorpDocApply(corpDocRequestDTO, file);
        return ResponseWrapper.success();
    }

    @Operation(summary = "get corpDoc apply", description = "법인서류 신청 상세정보 조회")
    @GetMapping("/{draftId}")
    public ApiResponse<CorpDocDetailResponseDTO> getCorpDocApply(@PathVariable("draftId") Long draftId) {
        return ResponseWrapper.success(corpDocService.getCorpDocApply(draftId));
    }

    @Operation(summary = "update corpDoc apply", description = "승인대기 중인 법인서류 신청 수정")
    @PostMapping("/update")
    public ApiResponse<?> updateCorpDocApply(
            @RequestParam("draftId") Long draftId,
            @RequestPart("corpDocUpdateRequest") CorpDocUpdateRequestDTO corpDocUpdateRequestDTO,
            @RequestPart(value = "file", required = false) MultipartFile file,
            boolean isFileDeleted) throws Exception {

        corpDocService.updateCorpDocApply(draftId, corpDocUpdateRequestDTO, file, isFileDeleted);
        return ResponseWrapper.success();
    }

    @Operation(summary = "cancel corpDoc apply", description = "법인서류 신청취소")
    @PutMapping("/{draftId}")
    public ApiResponse<?> cancelCorpDocApply(@PathVariable("draftId") Long draftId) {
        corpDocService.cancelCorpDocApply(draftId);
        return ResponseWrapper.success();
    }
}
