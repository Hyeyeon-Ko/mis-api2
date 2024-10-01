package kr.or.kmi.mis.api.doc.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.or.kmi.mis.api.doc.model.request.ReceiveDocRequestDTO;
import kr.or.kmi.mis.api.doc.model.request.SendDocRequestDTO;
import kr.or.kmi.mis.api.doc.model.request.DocUpdateRequestDTO;
import kr.or.kmi.mis.api.doc.model.response.DocDetailResponseDTO;
import kr.or.kmi.mis.api.doc.service.DocService;
import kr.or.kmi.mis.cmm.model.response.ApiResponse;
import kr.or.kmi.mis.cmm.model.response.ResponseWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/doc")
@RequiredArgsConstructor
@Tag(name = "DocApply", description = "문서수발신 신청 관련 API")
public class DocController {

    private final DocService docService;

    @Operation(summary = "create receive doc apply", description = "유저 > 문서수신 신청")
    @PostMapping("/receive")
    public ApiResponse<?> createReceiveDoc(
            @RequestPart("docRequest") ReceiveDocRequestDTO docRequestDTO,
            @RequestPart(value = "file", required = false) MultipartFile file) throws Exception {
        docService.applyReceiveDoc(docRequestDTO, file);
        return ResponseWrapper.success();
    }

    @Operation(summary = "create receive doc apply by leader", description = "팀원 제외 유저 > 문서수신 신청")
    @PostMapping("/receive/leader")
    public ApiResponse<?> createReceiveDocByLeader(
            @RequestPart("docRequest") ReceiveDocRequestDTO docRequestDTO,
            @RequestPart(value = "file", required = false) MultipartFile file) throws IOException {
        docService.applyReceiveDocByLeader(docRequestDTO, file);
        return ResponseWrapper.success();
    }

    @Operation(summary = "create send doc apply", description = "유저 > 문서발신 신청")
    @PostMapping("/send")
    public ApiResponse<?> createSendDoc(
            @RequestPart("docRequest") SendDocRequestDTO docRequestDTO,
            @RequestPart(value = "file", required = false) MultipartFile file) throws IOException {
        docService.applySendDoc(docRequestDTO, file);
        return ResponseWrapper.success();
    }

    @Operation(summary = "create send doc apply by leader", description = "팀원 제외 유저 > 문서발신 신청")
    @PostMapping("/send/leader")
    public ApiResponse<?> createSendDocByLeader(
            @RequestPart("docRequest") SendDocRequestDTO docRequestDTO,
            @RequestPart(value = "file", required = false) MultipartFile file) throws IOException {
        docService.applySendDocByLeader(docRequestDTO, file);
        return ResponseWrapper.success();
    }

    @Operation(summary = "modify doc apply", description = "유저 > 문서수발신 수정")
    @PostMapping(value = "/update")
    public ApiResponse<?> updateDocApply(
            @RequestParam("draftId") String draftId,
            @RequestPart("docUpdateRequest") DocUpdateRequestDTO docUpdateRequestDTO,
            @RequestPart(value = "file", required = false) MultipartFile file,
            @RequestParam(value = "isFileDeleted", defaultValue = "false") boolean isFileDeleted) throws IOException {
        docService.updateDocApply(draftId, docUpdateRequestDTO, file, isFileDeleted);
        return ResponseWrapper.success();
    }

    @Operation(summary = "cancel doc apply", description = "유저 > 문서수발신 취소")
    @PutMapping(value = "/{draftId}")
    public ApiResponse<?> cancelDoc(@PathVariable String draftId) {
        docService.cancelDocApply(draftId);
        return ResponseWrapper.success();
    }

    @Operation(summary = "get doc detail", description = "유저 > 문서수발신 상세 정보 조회")
    @GetMapping(value = "/{draftId}")
    public ApiResponse<DocDetailResponseDTO> getDocDetail(@PathVariable String draftId) {
        return ResponseWrapper.success(docService.getDoc(draftId));
    }
}
