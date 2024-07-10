package kr.or.kmi.mis.api.authority.controller;

import kr.or.kmi.mis.api.authority.model.response.AuthorityListResponseDTO;
import kr.or.kmi.mis.api.authority.model.response.MemberListResponseDTO;
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

    /* 권한 설정 페이지 */
    @GetMapping("/search")
    public ApiResponse<List<MemberListResponseDTO>> getMemberList(@RequestParam("center") String center) {
        return ResponseWrapper.success(authorityService.getMemberList(center));
    }

    /* 권한 추가 */
    @PostMapping("/admin")
    public ApiResponse<?> addAdmin(@RequestParam String instCd, @RequestParam String userId, @RequestParam String userRole) {
        authorityService.addAdmin(instCd, userId, userRole);
        return ResponseWrapper.success();
    }

    /* 권한 취소 */
    @DeleteMapping("/admin")
    public ApiResponse<?> deleteAdmin(@RequestParam("authID") Long id) {
        authorityService.deleteAdmin(id);
        return ResponseWrapper.success();
    }
}
