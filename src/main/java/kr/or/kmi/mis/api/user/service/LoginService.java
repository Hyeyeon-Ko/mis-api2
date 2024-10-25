package kr.or.kmi.mis.api.user.service;

import jakarta.servlet.http.HttpSession;
import kr.or.kmi.mis.api.user.model.request.LoginRequestDTO;
import kr.or.kmi.mis.api.user.model.request.SessionInfoRequestDTO;
import kr.or.kmi.mis.api.user.model.response.LoginResponseDTO;

public interface LoginService {

    /*로그인*/
    String login(LoginRequestDTO loginRequestDTO);

    /*사용자 로그인 상세정보 호출*/
    LoginResponseDTO getSessionInfo(SessionInfoRequestDTO sessionInfoRequestDTO);
}
