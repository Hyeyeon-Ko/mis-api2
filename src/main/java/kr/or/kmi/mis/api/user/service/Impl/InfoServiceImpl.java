package kr.or.kmi.mis.api.user.service.Impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import kr.or.kmi.mis.api.authority.model.response.ResponseData;
import kr.or.kmi.mis.api.exception.EntityNotFoundException;
import kr.or.kmi.mis.api.std.model.entity.StdDetail;
import kr.or.kmi.mis.api.std.model.entity.StdGroup;
import kr.or.kmi.mis.api.std.repository.StdDetailRepository;
import kr.or.kmi.mis.api.std.repository.StdGroupRepository;
import kr.or.kmi.mis.api.user.model.response.*;
import kr.or.kmi.mis.api.user.service.InfoService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InfoServiceImpl implements InfoService {

    private final HttpServletRequest request;
    private final ObjectMapper objectMapper;
    private final WebClient webClient;
    private final StdGroupRepository stdGroupRepository;
    private final StdDetailRepository stdDetailRepository;

    @Value("${external.orgChart.url}")
    private String externalOrgChartUrl;

    @Value("${external.userInfo.url}")
    private String externalUserInfoUrl;

    @Override
    @Transactional(readOnly = true)
    public InfoResponseDTO getUserInfo() {
        String currentUserId = (String) request.getSession().getAttribute("userId");
        String currentUser = (String) request.getSession().getAttribute("userNm");

        return InfoResponseDTO.of(currentUserId, currentUser);
    }

    @Override
    @Transactional(readOnly = true)
    public InfoDetailResponseDTO getUserInfoDetail(String userId) {

        if (userId == null) {
            userId = (String) request.getSession().getAttribute("userId");
        }

        var resultData = this.fetchUserInfo(userId).block();

        String userNm = Objects.requireNonNull(resultData).getUsernm();               // 성명
        String telNum = Objects.requireNonNull(resultData).getMpphonno();             // 전화번호
        String userEmail = Objects.requireNonNull(resultData).getEmail();             // 이메일
        String instCd = Objects.requireNonNull(resultData).getBzbzplceCd();           // 센터코드
        String instNm = Objects.requireNonNull(resultData).getBzbzplceNm();           // 센터명
        String teamCd = Objects.requireNonNull(resultData).getOrgdeptcd();            // 부서코드
        String deptNm = Objects.requireNonNull(resultData).getDeptname();             // 부서이름
        String roleNm = Objects.requireNonNull(resultData).getRolename();             // 직책
        String positionNm = Objects.requireNonNull(resultData).getPositionname();     // 직위

        return InfoDetailResponseDTO.of(userId, userNm, telNum, userEmail, instCd, instNm, teamCd, deptNm, roleNm, positionNm);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrgChartResponseDTO> getOrgChartInfo(String detailCd) {
        var resultData = fetchOrgChartInfo().block();
        if (resultData == null) {
            throw new IllegalArgumentException("Not Found");
        }

        return resultData.stream()
                .filter(data -> data.getDeptcode().equals(detailCd))
                .map(data -> OrgChartResponseDTO.builder()
                        .userId(data.getUserid())
                        .userNm(data.getUsername())
                        .deptCd(data.getDeptcode())
                        .roleNm(data.getRolename())
                        .positionNm(data.getPositionname())
                        .build())
                .collect(Collectors.toList());
    }



    @Override
    @Transactional(readOnly = true)
    public List<ConfirmResponseDTO> getConfirmInfo(String instCd) {

        StdGroup stdGroup = stdGroupRepository.findByGroupCd("B005")
                .orElseThrow(() -> new IllegalArgumentException("Not Found"));

        List<StdDetail> stdDetails = stdDetailRepository.findByGroupCdAndEtcItem1(stdGroup, instCd)
                .orElseThrow(() -> new IllegalArgumentException("Not Found"));

        return stdDetails.stream()
                .map(stdDetail -> {
                    String teamLeaderId = stdDetail.getEtcItem2();
                    String managerId = stdDetail.getEtcItem3();

                    InfoDetailResponseDTO teamLeaderInfo = getUserInfoDetail(teamLeaderId);
                    InfoDetailResponseDTO managerInfo = getUserInfoDetail(managerId);

                    return ConfirmResponseDTO.builder()
                            .teamLeaderId(teamLeaderId)
                            .teamLeaderNm(teamLeaderInfo.getUserName())
                            .teamLeaderDept(teamLeaderInfo.getDeptNm())
                            .teamLeaderRoleNm(teamLeaderInfo.getRoleNm())
                            .teamLeaderPositionNm(teamLeaderInfo.getPositionNm())
                            .managerId(managerId)
                            .managerNm(managerInfo.getUserName())
                            .managerRoleNm(managerInfo.getRoleNm())
                            .managerPositionNm(managerInfo.getPositionNm())
                            .managerDept(managerInfo.getDeptNm())
                            .build();
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public Mono<List<OrgChartResponseData.OrgChartData>> fetchOrgChartInfo() {
        return webClient.get()
                .uri(externalOrgChartUrl)
                .retrieve()
                .bodyToMono(String.class)
                .flatMap(responseJson -> {
                    if (responseJson == null) {
                        return Mono.error(new EntityNotFoundException("Org chart data not found"));
                    }
                    try {
                        OrgChartResponseData responseData = objectMapper.readValue(responseJson, OrgChartResponseData.class);
                        List<OrgChartResponseData.OrgChartData> resultData = responseData.getResultData();
                        if (resultData == null || resultData.isEmpty()) {
                            return Mono.error(new EntityNotFoundException("Org chart information not found"));
                        }
                        return Mono.just(resultData);
                    } catch (Exception e) {
                        return Mono.error(new RuntimeException("Error processing response from org chart API", e));
                    }
                });
    }

    @Override
    @Transactional
    public Mono<ResponseData.ResultData> fetchUserInfo(String userId) {
        Map<String, String> requestData = new HashMap<>();
        requestData.put("userId", userId);
        return webClient.post()
                .uri(externalUserInfoUrl)
                .bodyValue(requestData)
                .retrieve()
                .bodyToMono(String.class)
                .flatMap(responseJson -> {
                    if (responseJson == null) {
                        return Mono.error(new EntityNotFoundException("User response data not found for userId: " + userId));
                    }
                    try {
                        ResponseData responseData = objectMapper.readValue(responseJson, ResponseData.class);
                        ResponseData.ResultData resultData = responseData.getResultData();
                        if (resultData == null) {
                            return Mono.error(new EntityNotFoundException("User information not found for userId: " + userId));
                        }
                        return Mono.just(resultData);
                    } catch (JsonProcessingException e) {
                        System.err.println("Error parsing JSON response: " + e.getMessage());
                        return Mono.error(new RuntimeException("Failed to parse JSON from groupware API", e));
                    } catch (Exception e) {
                        System.err.println("General error: " + e.getMessage());
                        return Mono.error(new RuntimeException("General error while processing groupware API", e));
                    }

                });
    }
}
