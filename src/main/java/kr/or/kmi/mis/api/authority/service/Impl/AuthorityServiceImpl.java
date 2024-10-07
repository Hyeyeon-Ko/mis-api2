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
import kr.or.kmi.mis.api.std.service.StdDetailService;
import kr.or.kmi.mis.api.user.service.InfoService;
import kr.or.kmi.mis.cmm.model.entity.SessionExpiredException;
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
    private final StdDetailService stdDetailService;
    private final InfoService infoService;
    private final AuthorityQueryRepository authorityQueryRepository;
    private final StdDetailQueryRepository stdDetailQueryRepository;
    private final HttpServletRequest httpServletRequest;

    @Override
    public Page<AuthorityResponseDTO2> getAuthorityList2(Pageable page) {
        return authorityQueryRepository.getAuthorityList(page);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean hasStandardDataManagementAuthority() {

        StdGroup stdGroup = stdGroupRepository.findByGroupCd("B001")
                .orElseThrow(() -> new EntityNotFoundException("B001"));

        String sessionUserId = (String) httpServletRequest.getSession().getAttribute("userId");
        if (sessionUserId == null) {
            throw new SessionExpiredException("세션이 만료되었습니다. 다시 로그인해주세요.");
        }

        return stdDetailRepository.findByGroupCdAndDetailCd(stdGroup, sessionUserId).isPresent();
    }

    @Override
    @Transactional(readOnly = true)
    public String getMemberName(String userId) {

        String sessionUserId = (String) httpServletRequest.getSession().getAttribute("userId");
        if (sessionUserId == null) {
            throw new SessionExpiredException("세션이 만료되었습니다. 다시 로그인해주세요.");
        }

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
            String sessionUserId = (String) httpServletRequest.getSession().getAttribute("userId");
            if (sessionUserId == null) {
                throw new SessionExpiredException("세션이 만료되었습니다. 다시 로그인해주세요.");
            }
            newStdDetail.setRgstrId(sessionUserId);
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
        if (sessionUserId == null) {
            throw new SessionExpiredException("세션이 만료되었습니다. 다시 로그인해주세요.");
        }

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
                stdDetailService.deleteInfo("B001", stdDetail.getDetailCd());
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
        if(stdDetailRepository.existsByGroupCdAndDetailCd(stdGroup, authority.getUserId())){
            stdDetailService.deleteInfo(stdGroup.getGroupCd(), authority.getUserId());
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
