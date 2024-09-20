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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
@RequestMapping("/api/doc")
@RequiredArgsConstructor
@Tag(name = "DocApply", description = "문서수발신 신청 관련 API")
public class DocController {

    private final DocService docService;

    @Value("${file.upload-dir}")
    private String uploadDir;

    @Operation(summary = "create receive doc apply", description = "문서수신 신청")
    @PostMapping("/receive")
    public ApiResponse<?> createReceiveDoc(
            @RequestPart("docRequest") ReceiveDocRequestDTO docRequestDTO,
            @RequestPart(value = "file", required = false) MultipartFile file) throws IOException {
        docService.applyReceiveDoc(docRequestDTO, file);
        return ResponseWrapper.success();
    }

    @Operation(summary = "create send doc apply", description = "문서발신 신청")
    @PostMapping("/send")
    public ApiResponse<?> createSendDoc(
            @RequestPart("docRequest") SendDocRequestDTO docRequestDTO,
            @RequestPart(value = "file", required = false) MultipartFile file) throws IOException {
        docService.applySendDoc(docRequestDTO, file);
        return ResponseWrapper.success();
    }

    

    @Operation(summary = "modify doc apply", description = "문서수발신 수정")
    @PostMapping(value = "/update")
    public ApiResponse<?> updateDocApply(
            @RequestParam("draftId") Long draftId,
            @RequestPart("docUpdateRequest") DocUpdateRequestDTO docUpdateRequestDTO,
            @RequestPart(value = "file", required = false) MultipartFile file, boolean isFileDeleted) throws IOException {
        docService.updateDocApply(draftId, docUpdateRequestDTO, file, isFileDeleted);
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

    // 파일 다운로드 API
    @GetMapping("/download/{filename}")
    public ResponseEntity<Resource> downloadFile(@PathVariable String filename) {
        try {
            // 업로드 디렉터리에서 파일을 찾음
            Path filePath = Paths.get(uploadDir).resolve(filename).normalize();
            Resource resource = new UrlResource(filePath.toUri());

            // 파일이 존재할 경우 다운로드 가능하게 설정
            if (resource.exists()) {
                return ResponseEntity.ok()
                        .contentType(MediaType.APPLICATION_OCTET_STREAM)
                        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                        .body(resource);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }
        } catch (MalformedURLException ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

}
