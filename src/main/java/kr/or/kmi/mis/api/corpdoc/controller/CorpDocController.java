package kr.or.kmi.mis.api.corpdoc.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.or.kmi.mis.api.corpdoc.model.request.CorpDocRequestDTO;
import kr.or.kmi.mis.api.corpdoc.model.response.CorpDocDetailResponseDTO;
import kr.or.kmi.mis.api.corpdoc.service.CorpDocService;
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
@RequestMapping("/api/corpdoc")
@RequiredArgsConstructor
@Tag(name="CorpDocCRUD", description = "법인서류 관련 CRUD API")
public class CorpDocController {

    private final CorpDocService corpDocService;

    @Value("${file.upload-dir}")
    private String uploadDir;

    @Operation(summary = "create corpDoc apply", description = "법인서류 신청")
    @PostMapping(value = "/")
    public ApiResponse<?> createCorpDocApply(
            @RequestPart("corpDocRequest") CorpDocRequestDTO corpDocRequestDTO,
            @RequestPart(value = "file", required = false) MultipartFile file) throws IOException {

        corpDocService.createCorpDocApply(corpDocRequestDTO, file);
        return ResponseWrapper.success();
    }

    @Operation(summary = "get corpDoc apply", description = "법인서류 신청 상세정보 조회")
    @GetMapping(value = "/{draftId}")
    public ApiResponse<CorpDocDetailResponseDTO> getCorpDocApply(@PathVariable("draftId") Long draftId) {

        return ResponseWrapper.success(corpDocService.getCorpDocApply(draftId));
    }

    @Operation(summary = "update corpDoc apply", description = "승인대기 중인 법인서류 신청 수정")
    @PostMapping(value = "/update")
    public ApiResponse<?> updateCorpDocApply(
            @RequestParam("draftId") Long draftId,
            @RequestPart("corpDocRequest") CorpDocRequestDTO corpDocRequestDTO,
            @RequestPart(value = "file", required = false) MultipartFile file, boolean isFileDeleted) throws IOException {

        corpDocService.updateCorpDocApply(draftId, corpDocRequestDTO, file, isFileDeleted);
        return ResponseWrapper.success();
    }

    @Operation(summary = "cancel corpDoc apply", description = "법인서류 신청취소")
    @PutMapping(value = "/{draftId}")
    public ApiResponse<?> cancelCorpDocApply(@PathVariable("draftId") Long draftId) {

        corpDocService.cancelCorpDocApply(draftId);
        return ResponseWrapper.success();
    }

    @GetMapping("/download/{filename}")
    public ResponseEntity<Resource> downloadFile(@PathVariable("filename") String filename) {
        try {
            Path filePath = Paths.get(uploadDir).resolve(filename).normalize();
            org.springframework.core.io.Resource resource = new UrlResource(filePath.toUri());

            if (resource.exists()) {
                return ResponseEntity.ok()
                        .contentType(MediaType.APPLICATION_OCTET_STREAM)
                        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                        .body(resource);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }
        } catch (MalformedURLException exception) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}
