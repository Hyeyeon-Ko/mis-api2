package kr.or.kmi.mis.api.user.service.Impl;

import kr.or.kmi.mis.api.authority.model.entity.Authority;
import kr.or.kmi.mis.api.authority.model.response.ResponseData;
import kr.or.kmi.mis.api.authority.repository.AuthorityRepository;
import kr.or.kmi.mis.api.authority.service.AuthorityService;
import kr.or.kmi.mis.api.exception.EntityNotFoundException;
import kr.or.kmi.mis.api.std.model.entity.StdDetail;
import kr.or.kmi.mis.api.std.model.entity.StdGroup;
import kr.or.kmi.mis.api.std.repository.StdDetailRepository;
import kr.or.kmi.mis.api.std.repository.StdGroupRepository;
import kr.or.kmi.mis.api.user.model.request.LoginRequestDTO;
import kr.or.kmi.mis.api.user.model.response.LoginResponseDTO;
import kr.or.kmi.mis.api.user.service.LoginService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class LoginServiceImpl implements LoginService {

    private final WebClient.Builder webClientBuilder;
    private final AuthorityRepository authorityRepository;
    private final StdGroupRepository stdGroupRepository;
    private final StdDetailRepository stdDetailRepository;
    private final AuthorityService authorityService;

    @Value("${external.login.url}")
    private String externalLoginUrl;

    @Override
    public LoginResponseDTO login(LoginRequestDTO loginRequestDTO) {
        // userDetailInfo로부터 로그인 한 사람의 팀 이름 불러오기
        ResponseData.ResultData resultData = authorityService.fetchUserInfo(loginRequestDTO.getUserId()).block();
        String teamNm = Objects.requireNonNull(resultData).getOrgdeptnm();

        // 기준자료에서 팀이름을 이용해 사이드바 권한 가져오기
        StdGroup stdGroup = stdGroupRepository.findByGroupCd("B002")
                .orElseThrow(() -> new EntityNotFoundException("B002"));
        StdDetail stdDetail = stdDetailRepository.findByGroupCdAndDetailNm(stdGroup, teamNm)
                .orElseThrow(() -> new EntityNotFoundException("B002"));

        List<String> sidebarPermissions = Arrays.asList(
                stdDetail.getEtcItem1(), stdDetail.getEtcItem2(), stdDetail.getEtcItem3(),
                stdDetail.getEtcItem4(), stdDetail.getEtcItem5(), stdDetail.getEtcItem6()
        );

        Map responseMap = webClientBuilder.build()
                .post()
                .uri(externalLoginUrl)
                .bodyValue(loginRequestDTO)
                .retrieve()
                .bodyToMono(Map.class)
                .block();

        if (responseMap != null && "0000".equals(responseMap.get("resultCd"))) {
            LoginResponseDTO responseDTO = new LoginResponseDTO();
            responseDTO.setHngNm((String) responseMap.get("hngnm"));

            Authority authority = authorityRepository.findByUserIdAndDeletedtIsNull(loginRequestDTO.getUserId()).orElse(null);
            responseDTO.setRole(authority != null ? authority.getRole() : "USER");
            responseDTO.setSidebarPermissions(sidebarPermissions);

            return responseDTO;
        } else {
            return null;
        }
    }
}
