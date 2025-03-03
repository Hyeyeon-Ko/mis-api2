package kr.or.kmi.mis.api.user.service.Impl;

import kr.or.kmi.mis.api.authority.model.entity.Authority;
import kr.or.kmi.mis.api.authority.repository.AuthorityRepository;
import kr.or.kmi.mis.api.exception.EntityNotFoundException;
import kr.or.kmi.mis.api.std.model.entity.StdDetail;
import kr.or.kmi.mis.api.std.model.entity.StdGroup;
import kr.or.kmi.mis.api.std.repository.StdDetailRepository;
import kr.or.kmi.mis.api.std.repository.StdGroupRepository;
import kr.or.kmi.mis.api.user.model.request.LoginRequestDTO;
import kr.or.kmi.mis.api.user.model.request.SessionInfoRequestDTO;
import kr.or.kmi.mis.api.user.model.response.InfoDetailResponseDTO;
import kr.or.kmi.mis.api.user.model.response.LoginResponseDTO;
import kr.or.kmi.mis.api.user.model.response.OrgChartResponseData;
import kr.or.kmi.mis.api.user.service.InfoService;
import kr.or.kmi.mis.api.user.service.LoginService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.*;

@Service
@RequiredArgsConstructor
public class LoginServiceImpl implements LoginService {

    private final WebClient.Builder webClientBuilder;
    private final AuthorityRepository authorityRepository;
    private final StdGroupRepository stdGroupRepository;
    private final StdDetailRepository stdDetailRepository;
    private final InfoService infoService;

    @Value("${external.login.url}")
    private String externalLoginUrl;

    @Override
    @Transactional
    public String login(LoginRequestDTO loginRequestDTO) {

        Map responseMap = webClientBuilder.build()
                .post()
                .uri(externalLoginUrl)
                .bodyValue(loginRequestDTO)
                .retrieve()
                .bodyToMono(Map.class)
                .block();

        if (responseMap != null && "200".equals(responseMap.get("code").toString())) {
            Map<String, Object> dataMap = (Map<String, Object>) responseMap.get("data");

            if (dataMap != null) {
                return (String) dataMap.get("userNm");
            } else {
                throw new IllegalArgumentException("Login failed. Invalid credentials.");
            }
        } else {
            throw new IllegalArgumentException("Login failed. Invalid response from external API.");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public LoginResponseDTO getSessionInfo(SessionInfoRequestDTO sessionInfoRequestDTO) {

        // 1. 유저 상세정보 호출
        InfoDetailResponseDTO infoDetailResponseDTO = infoService.getUserInfoDetail(sessionInfoRequestDTO.getUserId());
        String instCd = infoDetailResponseDTO.getInstCd();
        String teamCd = infoDetailResponseDTO.getTeamCd();
        String roleNm = infoDetailResponseDTO.getRoleNm();

        LoginResponseDTO responseDTO = new LoginResponseDTO();
        responseDTO.setUserNm(sessionInfoRequestDTO.getUserNm());
        responseDTO.setInstCd(instCd);
        responseDTO.setTeamCd(teamCd);
        responseDTO.setRoleNm(roleNm);

        // teamCd, instCd -> deptCd
        StdGroup teamStdGroup = stdGroupRepository.findByGroupCd("A003")
                .orElseThrow(() -> new IllegalArgumentException("Not Found"));

        List<StdDetail> teamStdDetails = stdDetailRepository.findByGroupCdAndEtcItem3(teamStdGroup, responseDTO.getTeamCd())
                .orElseThrow(() -> new IllegalArgumentException("Not Found"));

        Optional<String> deptCdOpt = teamStdDetails.stream()
                .map(teamStdDetail -> {
                    try {
                        return stdDetailRepository.findByDetailCdAndEtcItem1(teamStdDetail.getEtcItem1(), responseDTO.getInstCd())
                                .map(StdDetail::getDetailCd)
                                .orElse(null);
                    } catch (Exception e) {
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .findFirst();

        String deptCd = deptCdOpt.orElseThrow(() -> new IllegalArgumentException("No matching department code found"));
        responseDTO.setDeptCd(deptCd);


        // 2. 기준자료에서 사번을 이용해 사이드바 권한 가져오기
        Authority authority = authorityRepository.findByUserId(sessionInfoRequestDTO.getUserId()).orElse(null);
        responseDTO.setRole(authority != null ? authority.getRole() : "USER");

        StdGroup stdGroup = stdGroupRepository.findByGroupCd("B002")
                .orElseThrow(() -> new EntityNotFoundException("B002"));

        Optional<StdDetail> stdDetailOpt = stdDetailRepository.findByGroupCdAndDetailCd(stdGroup, sessionInfoRequestDTO.getUserId());

        List<String> sidebarPermissions = null;
        if (stdDetailOpt.isPresent()) {
            StdDetail stdDetail = stdDetailOpt.get();
            sidebarPermissions = Arrays.asList(
                    stdDetail.getEtcItem2(), stdDetail.getEtcItem3(), stdDetail.getEtcItem4(),
                    stdDetail.getEtcItem5(), stdDetail.getEtcItem6(), stdDetail.getEtcItem7(), stdDetail.getEtcItem8()
            );
        }
        responseDTO.setSidebarPermissions(sidebarPermissions);

        // 3. 조직도상 부서코드 가져오기
        var resultData = infoService.fetchOrgChartInfo().block();
        if (resultData == null) {
            throw new IllegalArgumentException("Not Found");
        }

        String matchedDeptCode = resultData.stream()
                .filter(orgChartData -> orgChartData.getUserid().equals(sessionInfoRequestDTO.getUserId()))
                .map(OrgChartResponseData.OrgChartData::getDeptcode)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("User ID not found in org chart data"));

        responseDTO.setDeptCode(matchedDeptCode);

        return responseDTO;

    }
}
