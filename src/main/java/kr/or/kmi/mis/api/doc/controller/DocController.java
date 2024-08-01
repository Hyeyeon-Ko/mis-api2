package kr.or.kmi.mis.api.doc.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.or.kmi.mis.api.doc.model.request.DocRequestDTO;
import kr.or.kmi.mis.api.doc.model.request.DocUpdateRequestDTO;
import kr.or.kmi.mis.api.doc.model.response.DocDetailResponseDTO;
import kr.or.kmi.mis.api.doc.service.DocService;
import kr.or.kmi.mis.cmm.model.response.ApiResponse;
import kr.or.kmi.mis.cmm.model.response.ResponseWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/doc")
@RequiredArgsConstructor
@Tag(name = "DocApply", description = "문서수발신 신청 관련 API")
public class DocController {

    private final DocService docService;

    @Operation(summary = "create doc apply", description = "문서수발신 신청")
    @PostMapping
    public ApiResponse<?> createDocApply(@RequestBody DocRequestDTO docRequestDTO) {
        System.out.println("docRequestDTO.getInstCd() = " + docRequestDTO.getInstCd());
        docService.applyDoc(docRequestDTO);

        return ResponseWrapper.success();
    }

    @Operation(summary = "modify doc apply", description = "문서수발신 수정")
    @PostMapping(value = "/update")
    public ApiResponse<?> updateDocApply(@RequestParam("draftId") Long draftId, @RequestBody DocUpdateRequestDTO docUpdateRequestDTO) {
        docService.updateDocApply(draftId, docUpdateRequestDTO);

        return ResponseWrapper.success();
    }

    @Operation(summary = "cancel doc apply", description = "문서수발신 취소")
    @PutMapping(value = "/{draftId}")
    public ApiResponse<?> cancelDoc(@PathVariable Long draftId) {

        docService.cancelDocApply(draftId);

        return ResponseWrapper.success();
    }

    @Operation(summary = "get doc detail", description = "문서수발신 상세 정보 조회")
    @GetMapping(value = "/{draftId}")
    public ApiResponse<DocDetailResponseDTO> getBcdDetail(@PathVariable Long draftId) {

        return ResponseWrapper.success(docService.getDoc(draftId));
    }
}
