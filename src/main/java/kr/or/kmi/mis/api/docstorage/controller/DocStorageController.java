package kr.or.kmi.mis.api.docstorage.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.or.kmi.mis.api.docstorage.domain.entity.DocStorageDetail;
import kr.or.kmi.mis.api.docstorage.domain.request.DocStorageApplyRequestDTO;
import kr.or.kmi.mis.api.docstorage.domain.request.DocStorageRequestDTO;
import kr.or.kmi.mis.api.docstorage.domain.request.DocStorageUpdateRequestDTO;
import kr.or.kmi.mis.api.docstorage.domain.request.DocstorageFileRequestDTO;
import kr.or.kmi.mis.api.docstorage.service.DocStorageService;
import kr.or.kmi.mis.cmm.model.response.ApiResponse;
import kr.or.kmi.mis.cmm.model.response.ResponseWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/storage")
@RequiredArgsConstructor
@Tag(name="DocStorageCRUD", description = "문서보관목록 관련 CRUD API")
public class DocStorageController {

    private final DocStorageService docStorageService;

    @Operation(summary = "add docStorage info", description = "문서보관 관련 정보 추가")
    @PostMapping("/")
    public ApiResponse<?> addDocStorageInfo(@RequestBody DocStorageRequestDTO docStorageRequestDTO) {
        docStorageService.addStorageInfo(docStorageRequestDTO);

        return ResponseWrapper.success();
    }

    @Operation(summary = "modify docStorage info", description = "문서보관 관련 정보 수정")
    @PutMapping("/")
    public ApiResponse<?> updateDocStorageInfo(@RequestParam("detailId") Long detailId, @RequestBody DocStorageUpdateRequestDTO docStorageUpdateDTO) {
        docStorageService.updateStorageInfo(detailId, docStorageUpdateDTO);

        return ResponseWrapper.success();
    }

    @Operation(summary = "delete docStorage info", description = "문서보관 관련 정보 삭제")
    @DeleteMapping("/")
    public ApiResponse<?> deleteDocStorageInfo(@RequestParam("detailId") Long detailId) {
        docStorageService.deleteStorageInfo(detailId);

        return ResponseWrapper.success();
    }

    @Operation(summary = "get docStorage info", description = "문서보관 관련 정보 조회, 수정 시 사용")
    @GetMapping("/")
    public ApiResponse<?> getDocStorageInfo(@RequestParam("detailId") Long detailId) {

        return ResponseWrapper.success(docStorageService.getStorageInfo(detailId));
    }

    @Operation(summary = "apply docStorage", description = "문서보관 신청")
    @PostMapping("/apply")
    public ApiResponse<?> applyDocStorage(@RequestBody DocStorageApplyRequestDTO docStorageApplyRequestDTO) {
        docStorageService.applyStorage(docStorageApplyRequestDTO);

        return ResponseWrapper.success();
    }

    @Operation(summary = "upload docStorage", description = "엑셀 파일로 문서보관 일괄 신청 ")
    @PostMapping("/upload")
    public ApiResponse<?> uploadData(@RequestBody DocstorageFileRequestDTO request) {
        docStorageService.saveFileData(request.getDocuments(), request.getTeamCd());
        return ResponseWrapper.success();
    }

    @Operation(summary = "approve docStorage apply", description = "문서보관 신청 승인")
    @PutMapping("/approve")
    public ApiResponse<?> approveDocStorage(@RequestParam("draftIds") List<Long> draftIds) {
        docStorageService.approveStorage(draftIds);

        return ResponseWrapper.success();
    }

}