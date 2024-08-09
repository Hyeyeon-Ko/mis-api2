package kr.or.kmi.mis.api.authority.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.or.kmi.mis.api.authority.model.request.AuthorityRequestDTO;
import kr.or.kmi.mis.api.authority.model.response.AuthorityListResponseDTO;
import kr.or.kmi.mis.api.authority.model.response.AuthorityResponseDTO;
import kr.or.kmi.mis.api.authority.service.AuthorityService;
import kr.or.kmi.mis.cmm.model.response.ApiResponse;
import kr.or.kmi.mis.cmm.model.response.ResponseWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/auth")
@Tag(name = "Authority", description = "권한 관리 API")
public class AuthorityController {

    private final AuthorityService authorityService;

    @Operation(summary = "get authority list", description = "권한 관리 페이지에서 사용, 모든 권한 목록 호출")
    @GetMapping
    public ApiResponse<List<AuthorityListResponseDTO>> getAuthorityList() {
        return ResponseWrapper.success(authorityService.getAuthorityList());
    }

    @Operation(summary = "get member authority", description = "유저 ID를 통해 유저 기준자료 권한 조회")
    @GetMapping("/standardData")
    public ApiResponse<Boolean> getMemberAuthority() {
        return ResponseWrapper.success(authorityService.hasStandardDataManagementAuthority());
    }

    @Operation(summary = "get member name", description = "유저 ID를 통해 유저 이름 조회")
    @PostMapping
    public ApiResponse<String> getMemberName(@RequestParam String userId) {
        return ResponseWrapper.success(authorityService.getMemberName(userId));
    }

    @Operation(summary = "add admin", description = "새로운 관리 권한 추가")
    @PostMapping("/admin")
    public ApiResponse<?> addAdmin(@RequestBody AuthorityRequestDTO request) {
        authorityService.addAdmin(request);
        return ResponseWrapper.success();
    }

    @Operation(summary = "update admin", description = "기존 관리 권한 수정")
    @PutMapping("/admin/{authId}")
    public ApiResponse<?> updateAdmin(@PathVariable Long authId, @RequestBody AuthorityRequestDTO request) {
        authorityService.updateAdmin(authId, request);
        return ResponseWrapper.success();
    }

    @Operation(summary = "delete admin", description = "기존 관리 권한 삭제")
    @DeleteMapping("/admin/{authId}")
    public ApiResponse<?> deleteAdmin(@PathVariable Long authId) {
        authorityService.deleteAdmin(authId);
        return ResponseWrapper.success();
    }

    @Operation(summary = "get admin info", description = "특정 관리자 권한 정보 조회")
    @GetMapping("/admin/{authId}")
    public ApiResponse<AuthorityResponseDTO> getAdmin(@PathVariable Long authId) {
        return ResponseWrapper.success(authorityService.getAdmin(authId));
    }
}
