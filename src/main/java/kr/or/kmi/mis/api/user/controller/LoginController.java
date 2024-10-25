package kr.or.kmi.mis.api.user.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpSession;
import kr.or.kmi.mis.api.user.model.request.LoginRequestDTO;
import kr.or.kmi.mis.api.user.model.request.SessionInfoRequestDTO;
import kr.or.kmi.mis.api.user.model.response.LoginResponseDTO;
import kr.or.kmi.mis.api.user.service.LoginService;
import kr.or.kmi.mis.cmm.model.response.ApiResponse;
import kr.or.kmi.mis.cmm.model.response.ResponseWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Description;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/login")
@Tag(name = "Login", description = "로그인 API")
public class LoginController {
    private final LoginService loginService;

    @Operation(summary = "user login", description = "유저 로그인")
    @Description(value = "로그인 성공 시, 유저 이름 반환")
    @PostMapping
    public ApiResponse<?> login(@RequestBody LoginRequestDTO loginRequestDTO) {

        return ResponseWrapper.success(loginService.login(loginRequestDTO));
    }

    @Operation(summary = "login user session information", description = "유저 로그인 시, 반환되는 세션 정보")
    @PostMapping("/info")
    public ApiResponse<?> getSessionInfo(@RequestBody SessionInfoRequestDTO sessionInfoRequestDTO, HttpSession session) {

        LoginResponseDTO responseDTO = loginService.getSessionInfo(sessionInfoRequestDTO);

        if (responseDTO != null) {
            session.setAttribute("userId", sessionInfoRequestDTO.getUserId());
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
