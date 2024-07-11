package kr.or.kmi.mis.api.authority.controller;

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
public class AuthorityController {

    private final AuthorityService authorityService;

    /* 권한 관리 페이지 */
    @GetMapping
    public ApiResponse<List<AuthorityListResponseDTO>> getAuthorityList() {
        return ResponseWrapper.success(authorityService.getAuthorityList());
    }

    /* 권한 조회 */
    @PostMapping
    public ApiResponse<String> getMemberName(@RequestParam String userId) {
        return ResponseWrapper.success(authorityService.getMemberName(userId));
    }

    /* 권한 추가 */
    @PostMapping("/admin")
    public ApiResponse<?> addAdmin(@RequestParam String userRole, @RequestParam String userId) {
        authorityService.addAdmin(userRole, userId);
        return ResponseWrapper.success();
    }

    /*권한 수정*/
    @PostMapping("/admin/{authId}")
    public ApiResponse<?> modifyAdmin(@PathVariable Long authId, @RequestParam String userRole) {
        authorityService.modifyAdmin(authId, userRole);
        return ResponseWrapper.success();
    }

    /* 권한 취소 */
    @DeleteMapping("/admin/{authId}")
    public ApiResponse<?> deleteAdmin(@PathVariable Long authId) {
        authorityService.deleteAdmin(authId);
        return ResponseWrapper.success();
    }
}
