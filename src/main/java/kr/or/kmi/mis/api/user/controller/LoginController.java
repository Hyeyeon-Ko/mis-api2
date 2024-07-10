package kr.or.kmi.mis.api.user.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import kr.or.kmi.mis.api.user.model.request.LoginRequestDTO;
import kr.or.kmi.mis.api.user.model.response.LoginResponseDTO;
import kr.or.kmi.mis.api.user.service.LoginService;
import kr.or.kmi.mis.cmm.response.ApiResponse;
import kr.or.kmi.mis.cmm.response.ResponseWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/login")
public class LoginController {
    private final LoginService loginService;

    @PostMapping
    public ApiResponse<LoginResponseDTO> login(@RequestBody LoginRequestDTO loginRequestDTO, HttpServletRequest request) {
        LoginResponseDTO responseDTO = loginService.login(loginRequestDTO);

        // 로그인 성공 시 세션에 사용자 정보(사번, 이름) 저장
        if ("0000".equals(responseDTO.getResultCd())) {
            HttpSession session = request.getSession();
            session.setAttribute("userId", loginRequestDTO.getUserId());
            // session.setAttribute("hngNm", responseDTO.getHngNm());
            return ResponseWrapper.success(responseDTO);
        } else {
            return ResponseWrapper.error(responseDTO);
        }
    }
}
