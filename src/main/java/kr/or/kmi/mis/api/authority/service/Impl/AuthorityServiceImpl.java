package kr.or.kmi.mis.api.authority.service.Impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpSession;
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
    private final HttpSession httpSession;
    private final WebClient webClient;
//    private final WebClientUtils webClientUtils;

    @Value("${external.userInfo.url}")
    private String externalUserInfoUrl;

    @Override
    @Transactional(readOnly = true)
    public List<AuthorityListResponseDTO> getAuthorityList() {
        // 1. 종료일자가 찍히지 않은 admin, master 리스트 불러오기
        List<Authority> authorityList = authorityRepository.findAllByDeletedtIsNull();

        // 2. 각 관리자들의 상세 정보 불러오기
        // todo: 기준자료 생성 완료시 센터코드 -> 센터명으로 받아오기 수정!!!!
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

        String sessionUserId = (String) httpSession.getAttribute("userId");

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
    public void addAdmin(String userRole, String userId) {

        boolean authorityExists = authorityRepository.findByUserIdAndDeletedtIsNull(userId).isPresent();

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

    // todo: 관리 종류 추가시 수정 예정!!!!
    @Override
    public void modifyAdmin(Long authId, String role) {

        Authority authority = authorityRepository.findByAuthId(authId)
                .orElseThrow(() -> new EntityNotFoundException("Authority with authId " + authId + " not found"));

        authority.modifyAdmin(role);
    }

    @Override
    public void deleteAdmin(Long authId) {
        Authority authority = authorityRepository.findByAuthId(authId)
                .orElseThrow(() -> new EntityNotFoundException("Authority with id " + authId + " not found"));

        // 권한 취소할 관리자의 종료일시 저장
        authority.deleteAdmin(new Timestamp(System.currentTimeMillis()));
        authorityRepository.save(authority);
    }

    // 그룹웨어로부터 정보 불러오기
    private Mono<ResponseData.ResultData> fetchUserInfo(String userId) {
        // requestData 맵을 생성하고 userId 추가
        Map<String, String> requestData = new HashMap<>();
        requestData.put("userId", userId);

//        String response = webClientUtils.post(
//                "https://api.vitaport.co.kr/api/v1/kmi/setKmiExtExamCheckup",
//                requestData,
//                String.class
//        );


        // WebClient를 사용하여 외부 사용자 정보 API에 POST 요청
        return webClient.post()
                .uri(externalUserInfoUrl)  // 요청을 보낼 URL 설정
                .bodyValue(requestData)    // 요청 바디에 requestData 설정
                .retrieve()                // 응답을 받아옴
                .bodyToMono(String.class)  // 응답을 String으로 변환
                .flatMap(responseJson -> {
                    // 응답이 null이면 에러 반환
                    if (responseJson == null) {
                        return Mono.error(new EntityNotFoundException("User response data not found for userId: " + userId));
                    }
                    try {
                        // 응답 JSON을 ResponseData 객체로 변환
                        ResponseData responseData = objectMapper.readValue(responseJson, ResponseData.class);
                        // 변환된 ResponseData 객체에서 ResultData를 가져옴
                        ResponseData.ResultData resultData = responseData.getResultData();
                        // ResultData가 null이면 에러 반환
                        if (resultData == null) {
                            return Mono.error(new EntityNotFoundException("User information not found for userId: " + userId));
                        }
                        // ResultData가 유효하면 Mono.just로 반환
                        return Mono.just(resultData);
                    } catch (Exception e) {
                        // JSON 처리 중 에러가 발생하면 RuntimeException 반환
                        return Mono.error(new RuntimeException("Error processing response from groupware API", e));
                    }
                });
    }
}
