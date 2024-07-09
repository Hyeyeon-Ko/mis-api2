package kr.or.kmi.mis.api.user.service;

import kr.or.kmi.mis.api.user.model.request.LoginRequestDTO;
import kr.or.kmi.mis.api.user.model.response.LoginResponseDTO;

public interface LoginService {

    /* 로그인 */
    LoginResponseDTO login(LoginRequestDTO loginRequestDTO);
}
