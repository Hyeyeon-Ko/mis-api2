package kr.or.kmi.mis.api.authority.service.Impl;

import kr.or.kmi.mis.api.authority.model.entity.Authority;
import kr.or.kmi.mis.api.authority.model.response.AuthorityListResponseDTO;
import kr.or.kmi.mis.api.authority.model.response.MemberListResponseDTO;
import kr.or.kmi.mis.api.authority.repository.AuthorityRepository;
import kr.or.kmi.mis.api.authority.service.AuthorityService;
import kr.or.kmi.mis.api.exception.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.sql.Timestamp;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class
AuthorityServiceImpl implements AuthorityService {

    private final AuthorityRepository authorityRepository;
    private final RestTemplate restTemplate;

    @Override
    @Transactional(readOnly = true)
    public List<AuthorityListResponseDTO> getAuthorityList() {
        // 1. 권한이 ADMIN이고, 종료일자가 찍히지 않은 관리자 리스트 불러오기
        List<Authority> adminList = authorityRepository.findAllByRoleAndDeletedtIsNull("B");

        // 2. 각 관리자들의 상세 정보 불러오기
        return adminList.stream()
                .map(authority -> AuthorityListResponseDTO.builder()
                        .userId(authority.getUserId())
                        .hngNm(authority.getHngNm())
                        .instCd(authority.getInstCd())
                        .deptCd(authority.getDeptCd())
                        .deptNm(authority.getDeptNm())
                        .email(authority.getEmail())
                        .build())
                .collect(Collectors.toList());
    }

    // 그룹웨어에서 해당 센터의 총무팀 내역 불러오기 (이름, 사번)
    @Override
    public List<MemberListResponseDTO> getMemberList(String center) {
        // 그룹웨어 API 호출로 센터의 총무팀 내역을 불러오는 로직 필요
        return List.of();
    }

    @Override
    public void addAdmin(String instCd, String userId, String userRole) {
        // 그룹웨어 API 호출로 사번을 통해 사용자 정보를 가져오기
        // 권한은 ADMIN, MASTER 두가지
        //  -> 체크박스를 통해 상세 권하 설정할 예정

        // 예시
        // String groupwareUrl = "http://groupware.api/userinfo?userId=" + userId;
        // AuthorityRequestDTO userInfo = restTemplate.getForObject(groupwareUrl, AuthorityRequestDTO.class);

        // if (userInfo == null) {
        //     throw new EntityNotFoundException("User information not found for userId: " + userId);
        // }

        // AuthorityRequestDTO를 Authority Entity로 변환하여 저장
        // Authority authority = Authority.builder()
        //         .userId(userInfo.getUserId())
        //         .hngNm(userInfo.getHngNm())
        //         .instCd(userInfo.getInstCd())
        //         .deptCd(userInfo.getDeptCd())
        //         .deptNm(userInfo.getDeptNm())
        //         .email(userInfo.getEmail())
        //         .role(role)
        //         .createdt(new Timestamp(System.currentTimeMillis()))
        //         .build();

        // authorityRepository.save(authority);
    }

    @Override
    public void deleteAdmin(Long id) {
        Authority authority = authorityRepository.findByAuthId(id)
                .orElseThrow(() -> new EntityNotFoundException("Authority with id " + id + " not found"));

        // 권한 취소할 관리자의 종료일시 저장
        authority.deleteAdmin(new Timestamp(System.currentTimeMillis()));
        authorityRepository.save(authority);
    }
}
