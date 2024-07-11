package kr.or.kmi.mis.api.user.service.Impl;

import kr.or.kmi.mis.api.user.model.request.LoginRequestDTO;
import kr.or.kmi.mis.api.user.model.response.LoginResponseDTO;
import kr.or.kmi.mis.api.user.service.LoginService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class LoginServiceImpl implements LoginService {

    private final WebClient.Builder webClientBuilder;

    @Value("${external.login.url}")
    private String externalLoginUrl;

    @Override
    public LoginResponseDTO login(LoginRequestDTO loginRequestDTO) {
        Map responseMap = webClientBuilder.build()
                .post()
                .uri(externalLoginUrl)
                .bodyValue(loginRequestDTO)
                .retrieve()
                .bodyToMono(Map.class)
                .block();

        assert responseMap != null;
        if ("0000".equals(responseMap.get("resultCd"))) {
            LoginResponseDTO responseDTO = new LoginResponseDTO();
            responseDTO.setHngNm((String) responseMap.get("hngNm"));
            return responseDTO;
        } else {
            return null;
        }
    }
}
