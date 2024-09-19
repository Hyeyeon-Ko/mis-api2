package kr.or.kmi.mis.api.user.service.Impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import kr.or.kmi.mis.api.authority.service.AuthorityService;
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

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InfoServiceImpl implements InfoService {

    private final HttpServletRequest request;
    private final AuthorityService authorityService;
    private final ObjectMapper objectMapper;
    private final WebClient webClient;
    private final StdGroupRepository stdGroupRepository;
    private final StdDetailRepository stdDetailRepository;

    @Value("${external.orgChart.url}")
    private String externalOrgChartUrl;

    @Override
    @Transactional(readOnly = true)
    public InfoResponseDTO getUserInfo() {
        String currentUserId = (String) request.getSession().getAttribute("userId");
        String currentUser = (String) request.getSession().getAttribute("hngNm");

        return InfoResponseDTO.of(currentUserId, currentUser);
    }

    @Override
    @Transactional(readOnly = true)
    public InfoDetailResponseDTO getUserInfoDetail(String userId) {

        if (userId == null) {
            userId = (String) request.getSession().getAttribute("userId");
        }

        var resultData = authorityService.fetchUserInfo(userId).block();

        String userNm = Objects.requireNonNull(resultData).getUsernm();               // 성명
        String telNum = Objects.requireNonNull(resultData).getMpphonno();             // 전화번호
        String userEmail = Objects.requireNonNull(resultData).getEmail();             // 이메일
        String instCd = Objects.requireNonNull(resultData).getBzbzplceCd();           // 센터코드
        String teamCd = Objects.requireNonNull(resultData).getOrgdeptcd();            // 부서코드
        String deptNm = Objects.requireNonNull(resultData).getDeptname();             // 부서이름
        String roleNm = Objects.requireNonNull(resultData).getRolename();             // 직책
        String positionNm = Objects.requireNonNull(resultData).getPositionname();     // 직위

        return InfoDetailResponseDTO.of(userId, userNm, telNum, userEmail, instCd, teamCd, deptNm, roleNm, positionNm);
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
                        .positionNm(data.getPositionname())
                        .build())
                .collect(Collectors.toList());
    }



    @Override
    @Transactional(readOnly = true)
    public List<ConfirmResponseDTO> getConfirmInfo(String instCd) {

        StdGroup stdGroup = stdGroupRepository.findByGroupCd("C002")
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
}
