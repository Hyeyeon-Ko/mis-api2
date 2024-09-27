package kr.or.kmi.mis.api.file.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.or.kmi.mis.api.file.model.request.FileUploadRequestDTO;
import kr.or.kmi.mis.api.file.service.FileService;
import kr.or.kmi.mis.cmm.model.response.ApiResponse;
import kr.or.kmi.mis.cmm.model.response.ResponseWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/file")
@RequiredArgsConstructor
@Tag(name="FileCRUD", description = "파일 관련 CRUD API")
public class FileController {

    private final FileService fileService;

    @Operation(summary = "upload file", description = "파일 업로드")
    @PostMapping
    public ApiResponse<?> uploadFile(FileUploadRequestDTO fileUploadRequestDTO) {

        fileService.uploadFile(fileUploadRequestDTO);
        return ResponseWrapper.success();
    }
}
