package kr.or.kmi.mis.api.authority.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.or.kmi.mis.api.authority.model.response.AuthorityListResponseDTO;
import kr.or.kmi.mis.api.authority.service.AuthorityService;
import kr.or.kmi.mis.cmm.response.ApiResponse;
import kr.or.kmi.mis.cmm.response.ResponseWrapper;
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

    @Operation(summary = "get member name", description = "유저 ID를 통해 유저 이름 조회")
    @PostMapping
    public ApiResponse<String> getMemberName(@RequestParam String userId) {
        return ResponseWrapper.success(authorityService.getMemberName(userId));
    }

    @Operation(summary = "add admin", description = "새로운 관리 권한을 추가")
    @PostMapping("/admin")
    public ApiResponse<?> addAdmin(@RequestParam String userRole, @RequestParam String userId) {
        authorityService.addAdmin(userRole, userId);
        return ResponseWrapper.success();
    }

    @Operation(summary = "modify admin", description = "기존 관리 권한을 수정")
    @PostMapping("/admin/{authId}")
    public ApiResponse<?> modifyAdmin(@PathVariable Long authId, @RequestParam String userRole) {
        authorityService.modifyAdmin(authId, userRole);
        return ResponseWrapper.success();
    }

    @Operation(summary = "delete admin", description = "기존 관리 권한을 삭제")
    @DeleteMapping("/admin/{authId}")
    public ApiResponse<?> deleteAdmin(@PathVariable Long authId) {
        authorityService.deleteAdmin(authId);
        return ResponseWrapper.success();
    }
}
