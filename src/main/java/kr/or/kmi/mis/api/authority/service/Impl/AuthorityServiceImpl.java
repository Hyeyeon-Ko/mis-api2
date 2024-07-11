package kr.or.kmi.mis.api.authority.service.Impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.or.kmi.mis.api.authority.model.entity.Authority;
import kr.or.kmi.mis.api.authority.model.response.AuthorityListResponseDTO;
import kr.or.kmi.mis.api.authority.model.response.ResponseData;
import kr.or.kmi.mis.api.authority.repository.AuthorityRepository;
import kr.or.kmi.mis.api.authority.service.AuthorityService;
import kr.or.kmi.mis.api.exception.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class AuthorityServiceImpl implements AuthorityService {

    private final AuthorityRepository authorityRepository;
    private final ObjectMapper objectMapper;
    private final WebClient webClient;

    @Value("${external.userInfo.url}")
    private String externalUserInfoUrl;

    @Override
    @Transactional(readOnly = true)
    public List<AuthorityListResponseDTO> getAuthorityList() {
        // 1. 종료일자가 찍히지 않은 admin, master 리스트 불러오기
        List<Authority> authorityList = authorityRepository.findAllByDeletedtIsNull();

        // 2. 각 관리자들의 상세 정보 불러오기
        return authorityList.stream()
                .map(authority -> AuthorityListResponseDTO.builder()
                        .authId(authority.getAuthId())
                        .userId(authority.getUserId())
                        .hngNm(authority.getHngNm())
                        .userRole(authority.getRole())
                        .instCd(authority.getInstCd())
                        .deptNm(authority.getDeptNm())
                        .email(authority.getEmail())
                        .build())
                .collect(Collectors.toList());
    }


    @Override
    public String getMemberName(String userId) {

        ResponseData.ResultData resultData = fetchUserInfo(userId).block();
        if (resultData == null || resultData.getUsernm() == null) {
            throw new EntityNotFoundException("User information not found for userId: " + userId);
        }
        return resultData.getUsernm();
    }

    @Override
    public void addAdmin(String userRole, String userId) {

        boolean authorityExists = authorityRepository.findByUserId(userId).isPresent();

        if (authorityExists) {
            throw new IllegalStateException("Authority with userId " + userId + " already exists");
        }

        ResponseData.ResultData resultData = fetchUserInfo(userId).block();
        if (resultData == null) {
            throw new EntityNotFoundException("User information not found for userId: " + userId);
        }

        Authority authorityInfo = Authority.builder()
                .userId(resultData.getUserid())
                .hngNm(resultData.getUsernm())
                .instCd(resultData.getOrginstcd())
                .deptCd(resultData.getOrgdeptcd())
                .deptNm(resultData.getOrgdeptnm())
                .email(resultData.getEmail())
                .role(userRole)
                .createdt(new Timestamp(System.currentTimeMillis()))
                .build();

        authorityRepository.save(authorityInfo);
    }

    @Override
    public void deleteAdmin(Long authId) {
        Authority authority = authorityRepository.findByAuthId(authId)
                .orElseThrow(() -> new EntityNotFoundException("Authority with id " + authId + " not found"));

        // 권한 취소할 관리자의 종료일시 저장
        authority.deleteAdmin(new Timestamp(System.currentTimeMillis()));
        authorityRepository.save(authority);
    }

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
                        return Mono.error(new EntityNotFoundException("User information not found for userId: " + userId));
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
