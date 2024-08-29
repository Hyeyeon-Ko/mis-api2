package kr.or.kmi.mis.api.seal.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.or.kmi.mis.api.seal.model.request.SealRegisterRequestDTO;
import kr.or.kmi.mis.api.seal.model.request.SealUpdateRequestDTO;
import kr.or.kmi.mis.api.seal.service.SealRegisterService;
import kr.or.kmi.mis.cmm.model.response.ApiResponse;
import kr.or.kmi.mis.cmm.model.response.ResponseWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/seal/register")
@RequiredArgsConstructor
@Tag(name = "Seal Register", description = "인장 등록 관련 API")
public class SealRegisterController {

    private final SealRegisterService sealRegisterService;

    @Operation(summary = "create seal register", description = "인장 등록 시 사용")
    @PostMapping
    public ApiResponse<?> createSealRegister(@RequestBody SealRegisterRequestDTO sealRegisterRequestDTO) {

        sealRegisterService.registerSeal(sealRegisterRequestDTO);

        return ResponseWrapper.success();
    }

    @Operation(summary = "modify seal register", description = "등록한 인장 수정 시 사용")
    @PostMapping(value = "/update")
    public ApiResponse<?> updateSealRegister(@RequestParam Long draftId, @RequestBody SealUpdateRequestDTO sealUpdateRequestDTO) {

        sealRegisterService.updateSeal(draftId, sealUpdateRequestDTO);

        return ResponseWrapper.success();
    }

    @Operation(summary = "delete seal register", description = "등록한 인장 삭제 시 사용")
    @PutMapping(value = "/{draftId}")
    public ApiResponse<?> deleteSealRegister(@PathVariable Long draftId) {

        sealRegisterService.deleteSeal(draftId);

        return ResponseWrapper.success();
    }

}
