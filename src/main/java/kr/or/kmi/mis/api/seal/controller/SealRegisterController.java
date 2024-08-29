package kr.or.kmi.mis.api.seal.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.or.kmi.mis.api.seal.model.request.SealRegisterRequestDTO;
import kr.or.kmi.mis.api.seal.model.request.SealUpdateRequestDTO;
import kr.or.kmi.mis.api.seal.model.response.SealDetailResponseDTO;
import kr.or.kmi.mis.api.seal.service.SealRegisterService;
import kr.or.kmi.mis.cmm.model.response.ApiResponse;
import kr.or.kmi.mis.cmm.model.response.ResponseWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/seal/register")
@RequiredArgsConstructor
@Tag(name = "Seal Register", description = "인장 등록 관련 API")
public class SealRegisterController {

    private final SealRegisterService sealRegisterService;

    @Operation(summary = "create seal register", description = "인장 등록 시 사용")
    @PostMapping
    public ApiResponse<?> createSealRegister(@RequestPart SealRegisterRequestDTO sealRegisterRequestDTO,
                                             @RequestPart(value = "sealImage", required = false) MultipartFile sealImage) throws IOException {
        sealRegisterService.registerSeal(sealRegisterRequestDTO, sealImage);
        return ResponseWrapper.success();
    }

    @Operation(summary = "modify seal register", description = "등록한 인장 수정 시 사용")
    @PostMapping(value = "/update")
    public ApiResponse<?> updateSealRegister(@RequestParam Long draftId,
                                             @RequestPart SealUpdateRequestDTO sealUpdateRequestDTO,
                                             @RequestPart(value = "sealImage", required = false) MultipartFile sealImage,
                                             @RequestParam(value = "isFileDeleted", defaultValue = "false") boolean isFileDeleted) throws IOException {
        sealRegisterService.updateSeal(draftId, sealUpdateRequestDTO, sealImage, isFileDeleted);
        return ResponseWrapper.success();
    }

    @Operation(summary = "delete seal register", description = "등록한 인장 삭제 시 사용")
    @DeleteMapping(value = "/{draftId}")
    public ApiResponse<?> deleteSealRegister(@PathVariable Long draftId) {
        sealRegisterService.deleteSeal(draftId);
        return ResponseWrapper.success();
    }

    @Operation(summary = "get seal detail", description = "등록한 인장 상세조회")
    @GetMapping(value = "/{draftId}")
    public ApiResponse<SealDetailResponseDTO> getSealDetail(@PathVariable Long draftId) {
        return ResponseWrapper.success(sealRegisterService.getSealDetail(draftId));
    }
}
