package kr.or.kmi.mis.api.user.service.Impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.or.kmi.mis.api.user.model.request.LoginRequestDTO;
import kr.or.kmi.mis.api.user.model.response.LoginResponseDTO;
import kr.or.kmi.mis.api.user.service.LoginService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class LoginServiceImpl implements LoginService {

    // RestTemplate 는 srping 3버전에 사용되었고 현재도 사용중이긴 하나 동기형 방식과 비반응형 클라이언트입니다.
    // 사용하는 것을 지양합니다. 다른 방식이 있는데 사용하시면 좋을거같아요.
    // 해당 부분은 꼭 확인하고 다른 방식을 사용해야합니다.
    private final RestTemplate restTemplate;

    @Value("${external.login.url}")
    private String externalLoginUrl;

    @Override
    public LoginResponseDTO login(LoginRequestDTO loginRequestDTO) {
        HttpEntity<LoginRequestDTO> request = new HttpEntity<>(loginRequestDTO);
        ResponseEntity<String> response = restTemplate.exchange(
                externalLoginUrl,
                HttpMethod.POST,
                request,
                String.class
        );

        LoginResponseDTO responseDTO = new LoginResponseDTO();
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            responseDTO = objectMapper.readValue(response.getBody(), LoginResponseDTO.class);
        } catch (Exception e) {
            responseDTO.setResultCd("9999");
            // responseDTO.setHngNm("null");
            responseDTO.setResultMsg("로그인 실패");
        }

        return responseDTO;
    }
}
