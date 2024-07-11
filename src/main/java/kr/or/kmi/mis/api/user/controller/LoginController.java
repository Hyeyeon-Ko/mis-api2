package kr.or.kmi.mis.api.user.controller;

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
    public ApiResponse<?> login(@RequestBody LoginRequestDTO loginRequestDTO, HttpSession session) {
        LoginResponseDTO responseDTO = loginService.login(loginRequestDTO);

        if (responseDTO != null) {
            session.setAttribute("userId", loginRequestDTO.getUserId());
            session.setAttribute("hngNm", responseDTO.getHngNm());
            return ResponseWrapper.success();
        } else {
            return ResponseWrapper.error();
        }
    }
}
