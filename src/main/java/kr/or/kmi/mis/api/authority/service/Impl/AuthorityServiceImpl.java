package kr.or.kmi.mis.api.authority.service.Impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.or.kmi.mis.api.authority.model.entity.Authority;
import kr.or.kmi.mis.api.authority.model.request.AuthorityRequestDTO;
import kr.or.kmi.mis.api.authority.model.response.AuthorityListResponseDTO;
import kr.or.kmi.mis.api.authority.model.response.ResponseData;
import kr.or.kmi.mis.api.authority.repository.AuthorityRepository;
import kr.or.kmi.mis.api.authority.service.AuthorityService;
import kr.or.kmi.mis.api.exception.EntityNotFoundException;
import kr.or.kmi.mis.api.std.model.entity.StdDetail;
import kr.or.kmi.mis.api.std.model.entity.StdGroup;
import kr.or.kmi.mis.api.std.repository.StdDetailRepository;
import kr.or.kmi.mis.api.std.repository.StdGroupRepository;
import kr.or.kmi.mis.api.user.service.InfoService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.sql.Timestamp;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class AuthorityServiceImpl implements AuthorityService {

    private final InfoService infoService;
    private final AuthorityRepository authorityRepository;
    private final StdDetailRepository stdDetailRepository;
    private final StdGroupRepository stdGroupRepository;
    private final ObjectMapper objectMapper;
    private final WebClient webClient;

    @Value("${external.userInfo.url}")
    private String externalUserInfoUrl;

    @Override
    @Transactional(readOnly = true)
    public List<AuthorityListResponseDTO> getAuthorityList() {
        List<Authority> authorityList = authorityRepository.findAllByDeletedtIsNull();
        return authorityList.stream()
                .map(authority -> {
                    StdDetail stdDetail1 = stdDetailRepository.findByDetailCd(authority.getInstCd())
                            .orElseThrow(() -> new EntityNotFoundException(authority.getInstCd()));
                    StdGroup stdGroup = stdGroupRepository.findByGroupCd("B001")
                            .orElseThrow(() -> new EntityNotFoundException("B001"));

                    Optional<StdDetail> optionalStdDetail2 = stdDetailRepository.findByGroupCdAndDetailNm(stdGroup, authority.getUserId());

                    return AuthorityListResponseDTO.builder()
                            .authId(authority.getAuthId())
                            .userId(authority.getUserId())
                            .hngNm(authority.getHngNm())
                            .userRole(authority.getRole())
                            .deptNm(authority.getDeptNm())
                            .email(authority.getEmail())
                            .instNm(stdDetail1.getDetailNm())
                            .detailCd(optionalStdDetail2.map(StdDetail::getDetailCd).orElse(null))
                            .build();
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public String getMemberName(String userId) {
        String sessionUserId = infoService.getUserInfo().getCurrentUserId();
        if (sessionUserId.equals(userId)) {
            throw new RuntimeException("로그인한 사용자의 userId와 동일합니다");
        }

        ResponseData.ResultData resultData = fetchUserInfo(userId).block();
        if (resultData == null || resultData.getUsernm() == null) {
            throw new EntityNotFoundException("User information not found for userId: " + userId);
        }
        return resultData.getUsernm();
    }

    @Override
    @Transactional
    public void addAdmin(AuthorityRequestDTO request) {
        boolean authorityExists = authorityRepository.findByUserIdAndDeletedtIsNull(request.getUserId()).isPresent();
        if (authorityExists) {
            throw new IllegalStateException("Authority with userId " + request.getUserId() + " already exists");
        }

        ResponseData.ResultData resultData = fetchUserInfo(request.getUserId()).block();
        if (resultData == null) {
            throw new EntityNotFoundException("User information not found for userId: " + request.getUserId());
        }

        Authority authorityInfo = Authority.builder()
                .userId(resultData.getUserid())
                .hngNm(resultData.getUsernm())
                .instCd(resultData.getOrginstcd())
                .deptCd(resultData.getOrgdeptcd())
                .deptNm(resultData.getOrgdeptnm())
                .email(resultData.getEmail())
                .role(request.getUserRole())
                .createdt(new Timestamp(System.currentTimeMillis()))
                .build();

        if (request.getDetailRole() != null && request.getDetailRole().equals("Y")) {
            StdDetail newStdDetail = StdDetail.builder()
                    .detailCd(request.getUserId())
                    .groupCd(stdGroupRepository.findById("B001").orElseThrow(() -> new IllegalArgumentException("Invalid groupCd")))
                    .detailNm("기준자료")
                    .fromDd(null)
                    .toDd(null)
                    .etcItem1(request.getUserId())
                    .etcItem2(request.getUserNm())
                    .build();
            newStdDetail.setRgstDt(new Timestamp(System.currentTimeMillis()));
            stdDetailRepository.save(newStdDetail);
        }

        authorityRepository.save(authorityInfo);
    }

    @Override
    @Transactional
    public void updateAdmin(Long authId, AuthorityRequestDTO request) {
        Authority authority = authorityRepository.findByAuthId(authId)
                .orElseThrow(() -> new EntityNotFoundException("Authority with authId " + authId + " not found"));
        authority.updateAdmin(request.getUserRole());
        authorityRepository.save(authority);

        String sessionUserId = infoService.getUserInfo().getCurrentUserId();

        if (request.getDetailRole() != null && request.getDetailRole().equals("Y")) {
            StdDetail stdDetail = stdDetailRepository.findByEtcItem1(authority.getUserId())
                    .orElseGet(() -> StdDetail.builder()
                            .detailCd(authority.getUserId())
                            .groupCd(stdGroupRepository.findById("B001").orElseThrow(() -> new IllegalArgumentException("Invalid groupCd")))
                            .detailNm(authority.getUserId())
                            .fromDd(null)
                            .toDd(null)
                            .etcItem1(authority.getUserId())
                            .etcItem2(authority.getHngNm())
                            .build()
                    );
            stdDetail.setRgstDt(new Timestamp(System.currentTimeMillis()));
            stdDetail.setRgstrId(sessionUserId);
            stdDetailRepository.save(stdDetail);
        } else {
            stdDetailRepository.findByEtcItem1(authority.getUserId()).ifPresent(stdDetail -> {
                stdDetail.updateUseAt("N");
                stdDetail.setUpdtDt(new Timestamp(System.currentTimeMillis()));
                stdDetail.setUpdtrId(sessionUserId);
                stdDetailRepository.save(stdDetail);
            });
        }
    }

    @Override
    @Transactional
    public void deleteAdmin(Long authId, String detailCd) {
        Authority authority = authorityRepository.findByAuthId(authId)
                .orElseThrow(() -> new EntityNotFoundException("Authority with id " + authId + " not found"));

        // 권한 테이블 -> 취소할 관리자의 종료일시 저장
        authority.deleteAdmin(new Timestamp(System.currentTimeMillis()));
        authorityRepository.save(authority);

        // 기준자료 관리자였다면 -> 기준자료 테이블 updateUseAt("N")
        if (detailCd != null) {
            StdDetail stdDetail = stdDetailRepository.findById(detailCd)
                    .orElseThrow(() -> new IllegalArgumentException("Invalid detailCd: " + detailCd));
            stdDetail.updateUseAt("N");
            stdDetailRepository.save(stdDetail);
        }
    }

    // 외부 사용자 정보 API에서 사용자 정보 가져오기
    private Mono<ResponseData.ResultData> fetchUserInfo(String userId) {
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
                    } catch (Exception e) {
                        return Mono.error(new RuntimeException("Error processing response from groupware API", e));
                    }
                });
    }
}
