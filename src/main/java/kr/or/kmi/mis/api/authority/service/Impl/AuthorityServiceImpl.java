package kr.or.kmi.mis.api.authority.service.Impl;

import jakarta.servlet.http.HttpServletRequest;
import kr.or.kmi.mis.api.authority.model.entity.Authority;
import kr.or.kmi.mis.api.authority.model.request.AuthorityRequestDTO;
import kr.or.kmi.mis.api.authority.model.response.AuthorityResponseDTO;
import kr.or.kmi.mis.api.authority.model.response.AuthorityResponseDTO2;
import kr.or.kmi.mis.api.authority.model.response.ResponseData;
import kr.or.kmi.mis.api.authority.repository.AuthorityQueryRepository;
import kr.or.kmi.mis.api.authority.repository.AuthorityRepository;
import kr.or.kmi.mis.api.authority.service.AuthorityService;
import kr.or.kmi.mis.api.exception.EntityNotFoundException;
import kr.or.kmi.mis.api.std.model.entity.StdDetail;
import kr.or.kmi.mis.api.std.model.entity.StdGroup;
import kr.or.kmi.mis.api.std.repository.StdDetailQueryRepository;
import kr.or.kmi.mis.api.std.repository.StdDetailRepository;
import kr.or.kmi.mis.api.std.repository.StdGroupRepository;
import kr.or.kmi.mis.api.user.service.InfoService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Transactional
@RequiredArgsConstructor
public class AuthorityServiceImpl implements AuthorityService {

    private final AuthorityRepository authorityRepository;
    private final StdDetailRepository stdDetailRepository;
    private final StdGroupRepository stdGroupRepository;
    private final InfoService infoService;
    private final AuthorityQueryRepository authorityQueryRepository;
    private final StdDetailQueryRepository stdDetailQueryRepository;
    private final HttpServletRequest httpServletRequest;

    @Override
    public Page<AuthorityResponseDTO2> getAuthorityList2(Pageable page) {
        return authorityQueryRepository.getAuthorityList(page);
    }
//    @Override
//    @Transactional(readOnly = true)
//    public List<AuthorityListResponseDTO> getAuthorityList() {
//        List<Authority> authorityList = authorityRepository.findAllByDeletedtIsNull();
//        return authorityList.stream()
//                .map(authority -> {
//                    // 기준자료 참조
//                    // 1. 팀 이름 -> 부서 코드
//                    StdGroup stdGroup1 = stdGroupRepository.findByGroupCd("A003")
//                            .orElseThrow(() -> new EntityNotFoundException("A003"));
//
//                    // 여러 개의 StdDetail을 리스트로 받아옴
//                    List<StdDetail> stdDetails = stdDetailRepository.findByGroupCdAndDetailNm(stdGroup1, authority.getDeptNm())
//                            .orElseThrow(() -> new IllegalArgumentException("Not Found"));
//
//                    if (stdDetails.isEmpty()) {
//                        throw new EntityNotFoundException("No StdDetail found for DeptNm: " + authority.getDeptNm());
//                    }
//
//                    // A002 그룹 코드의 StdDetail 찾기
//                    Optional<StdDetail> matchingDetailOpt = stdDetails.stream()
//                            .map(stdDetail -> {
//                                StdGroup stdGroup2 = stdGroupRepository.findByGroupCd("A002")
//                                        .orElseThrow(() -> new EntityNotFoundException("A002"));
//
//                                return stdDetailRepository.findByGroupCdAndDetailCdAndEtcItem1(stdGroup2, stdDetail.getEtcItem1(), authority.getInstCd())
//                                        .orElse(null);
//                            })
//                            .filter(Objects::nonNull)
//                            .findFirst();
//
//                    if (matchingDetailOpt.isEmpty()) {
//                        throw new EntityNotFoundException("No matching StdDetail found for DeptCd and InstCd");
//                    }
//
//                    StdDetail matchingDetail = matchingDetailOpt.get();
//                    String deptNm = matchingDetail.getDetailNm();
//                    String instCd = matchingDetail.getEtcItem1();
//
//                    // 3. 센터 코드 -> 센터 이름
//                    StdGroup stdGroup3 = stdGroupRepository.findByGroupCd("A001")
//                            .orElseThrow(() -> new EntityNotFoundException("A001"));
//                    StdDetail stdDetail3 = stdDetailRepository.findByGroupCdAndDetailCd(stdGroup3, instCd)
//                            .orElseThrow(() -> new EntityNotFoundException(instCd));
//                    String instNm = stdDetail3.getDetailNm();
//
//                    return AuthorityListResponseDTO.builder()
//                            .authId(authority.getAuthId())
//                            .userId(authority.getUserId())
//                            .hngNm(authority.getHngNm())
//                            .userRole(authority.getRole())
//                            .email(authority.getEmail())
//                            .instNm(instNm)
//                            .deptNm(deptNm)
//                            .detailCd(authority.getUserId())
//                            .build();
//                })
//                .collect(Collectors.toList());
//    }

    @Override
    @Transactional(readOnly = true)
    public boolean hasStandardDataManagementAuthority() {

        StdGroup stdGroup = stdGroupRepository.findByGroupCd("B001")
                .orElseThrow(() -> new EntityNotFoundException("B001"));

        String sessionUserId = (String) httpServletRequest.getSession().getAttribute("userId");

        return stdDetailRepository.findByGroupCdAndDetailCd(stdGroup, sessionUserId).isPresent();
    }

    @Override
    @Transactional(readOnly = true)
    public String getMemberName(String userId) {

        String sessionUserId = (String) httpServletRequest.getSession().getAttribute("userId");

        if (sessionUserId.equals(userId)) {
            throw new IllegalArgumentException("로그인한 사용자의 userId와 동일합니다");
        }

        ResponseData.ResultData resultData = infoService.fetchUserInfo(userId).block();
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

        ResponseData.ResultData resultData = infoService.fetchUserInfo(request.getUserId()).block();
        if (resultData == null) {
            throw new EntityNotFoundException("User information not found for userId: " + request.getUserId());
        }
        
        String deptCd = stdDetailQueryRepository.findDetailCd(resultData.getOrgdeptcd(), resultData.getBzbzplceCd());

        Authority authorityInfo = Authority.builder()
                .userId(resultData.getUserid())
                .hngNm(resultData.getUsernm())
                .instCd(resultData.getBzbzplceCd())
                .deptCd(deptCd)
                .teamCd(resultData.getOrgdeptcd())
                .teamNm(resultData.getOrgdeptnm())
                .email(resultData.getEmail())
                .role(request.getUserRole())
                .createdt(LocalDateTime.now())
                .build();

        if (request.getDetailRole() != null && request.getDetailRole().equals("Y")) {
            StdDetail newStdDetail = StdDetail.builder()
                    .detailCd(request.getUserId())
                    .groupCd(stdGroupRepository.findById("B001").orElseThrow(() -> new IllegalArgumentException("Invalid groupCd")))
                    .detailNm(request.getUserNm())
                    .etcItem1(request.getUserId())
                    .etcItem2(request.getUserRole())
                    .build();
            newStdDetail.setRgstrId((String) httpServletRequest.getSession().getAttribute("userId"));
            newStdDetail.setRgstDt(LocalDateTime.now());
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

        String sessionUserId = (String) httpServletRequest.getSession().getAttribute("userId");

        if (request.getDetailRole() != null && request.getDetailRole().equals("Y")) {
            StdDetail stdDetail = stdDetailRepository.findByEtcItem1(authority.getUserId())
                    .orElseGet(() -> StdDetail.builder()
                            .detailCd(authority.getUserId())
                            .groupCd(stdGroupRepository.findById("B001").orElseThrow(() -> new IllegalArgumentException("Invalid groupCd")))
                            .detailNm(authority.getHngNm())
                            .etcItem1(authority.getUserId())
                            .etcItem2(authority.getRole())
                            .build()
                    );
            stdDetail.setRgstDt(LocalDateTime.now());
            stdDetail.setRgstrId(sessionUserId);
            stdDetailRepository.save(stdDetail);
        } else {
            stdDetailRepository.findByEtcItem1(authority.getUserId()).ifPresent(stdDetail -> {
                stdDetailRepository.deleteByGroupCdAndDetailCd(stdDetail.getGroupCd(), stdDetail.getDetailCd());
            });
        }
    }

    @Override
    @Transactional
    public void deleteAdmin(Long authId) {
        Authority authority = authorityRepository.findByAuthId(authId)
                .orElseThrow(() -> new EntityNotFoundException("Authority with id " + authId + " not found"));

        // 권한 테이블 -> 취소할 관리자의 종료일시 저장
        authority.deleteAdmin(LocalDateTime.now());
        authorityRepository.save(authority);

        StdGroup stdGroup = stdGroupRepository.findByGroupCd("B001")
                .orElseThrow(() -> new EntityNotFoundException("B001"));

        // 기준자료 관리자였다면 -> 기준자료 테이블 데이터 삭제
        if (authority.getUserId() != null) {
            stdDetailRepository.findByGroupCdAndDetailCd(stdGroup, authority.getUserId())
                    .ifPresent(stdDetailToDelete -> stdDetailRepository.deleteByGroupCdAndDetailCd(
                            stdDetailToDelete.getGroupCd(), stdDetailToDelete.getDetailCd()));
        }
    }

    @Override
    @Transactional(readOnly = true)
    public AuthorityResponseDTO getAdmin(Long authId) {

        Authority authority = authorityRepository.findByAuthId(authId)
                .orElseThrow(() -> new EntityNotFoundException("Authority with id " + authId + " not found"));

        StdGroup stdGroup = stdGroupRepository.findByGroupCd("B001")
                .orElseThrow(() -> new EntityNotFoundException("StdGroup with id " + authId + " not found"));

        String canHandleStd = "N";
        if (stdDetailRepository.findByGroupCdAndDetailCd(stdGroup, authority.getUserId()).isPresent()) {
            canHandleStd = "Y";
        }

        return AuthorityResponseDTO.of(authority, canHandleStd);
    }
}
