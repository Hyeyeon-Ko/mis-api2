package kr.or.kmi.mis.api.user.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpSession;
import kr.or.kmi.mis.api.user.model.request.LoginRequestDTO;
import kr.or.kmi.mis.api.user.model.response.LoginResponseDTO;
import kr.or.kmi.mis.api.user.service.LoginService;
import kr.or.kmi.mis.cmm.model.response.ApiResponse;
import kr.or.kmi.mis.cmm.model.response.ResponseWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/login")
@Tag(name = "Login", description = "로그인 API")
public class LoginController {
    private final LoginService loginService;

    @Operation(summary = "user login", description = "유저 로그인")
    @PostMapping
    public ApiResponse<?> login(@RequestBody LoginRequestDTO loginRequestDTO, HttpSession session) {

        LoginResponseDTO responseDTO = loginService.login(loginRequestDTO);

        if (responseDTO != null) {
            session.setAttribute("userId", loginRequestDTO.getUserId());
            session.setAttribute("userNm", responseDTO.getUserNm());
            session.setAttribute("role", responseDTO.getRole());
            session.setAttribute("instCd", responseDTO.getInstCd());
            session.setAttribute("deptCd", responseDTO.getDeptCd());
            session.setAttribute("teamCd", responseDTO.getTeamCd());
            return ResponseWrapper.success(responseDTO);
        } else {
            return ResponseWrapper.error();
        }
    }
}
