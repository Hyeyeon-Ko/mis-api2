package kr.or.kmi.mis.api.authority.controller;

import kr.or.kmi.mis.api.authority.model.request.AuthorityRequestDTO;
import kr.or.kmi.mis.api.authority.model.response.AuthorityListResponseDTO;
import kr.or.kmi.mis.api.authority.service.AuthorityService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/auth")
public class AuthorityController {

    private AuthorityService authorityService;

    /* 권한 관리 페이지 */
    @GetMapping
    public ResponseEntity<List<AuthorityListResponseDTO>> getAuthorityList() {
        List<AuthorityListResponseDTO> authorityList = authorityService.getAuthorityList();
        return ResponseEntity.status(HttpStatus.OK).body(authorityList);
    }

    /* 권한 설정 페이지 */
//    그룹웨어에서 불러와야 하므로 나중에 구현
//    @GetMapping("/search")
//    public ResponseEntity<List<MemberListResponse>> getMemberList(@RequestParam("center") String center) {
//        List<MemberListResponse> memberList = authorityService.getMemberList(center);
//        return ResponseEntity.status(HttpStatus.OK).body(memberList);
//    }

    /* 권한 추가 */
    @PostMapping("/admin")
    public ResponseEntity<String> addAdmin(@RequestBody AuthorityRequestDTO authorityRequestDTO) {
        authorityService.addAdmin(authorityRequestDTO);
        return new ResponseEntity<>("ok", HttpStatus.OK);
    }

    /* 권한 취소 */
    @DeleteMapping("/admin")
    public ResponseEntity<String> deleteAdmin(@RequestParam("authID") Long id) {
        authorityService.deleteAdmin(id);
        return new ResponseEntity<>("ok", HttpStatus.OK);
    }
}
