package kr.or.kmi.mis.api.authority.service;

import kr.or.kmi.mis.api.authority.model.entity.Authority;
import kr.or.kmi.mis.api.authority.model.request.AuthorityRequestDTO;
import kr.or.kmi.mis.api.authority.model.response.AuthorityListResponseDTO;
import kr.or.kmi.mis.api.authority.repository.AuthorityRepository;
import kr.or.kmi.mis.api.exception.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class AuthorityService {

    private final AuthorityRepository authorityRepository;

    /* 관리자 목록 불러오기 */
    @Transactional(readOnly = true)
    public List<AuthorityListResponseDTO> getAuthorityList() {
        // 1. 권한이 ADMIN이고, 종료일자가 찍히지 않은 관리자 리스트 불러오기
        List<Authority> adminList = authorityRepository.findAllByRoleAndDeletedtIsNull("B");

        // 2. 각 관리자들의 상세 정보 불러오기 using Streams
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

    /* 권한 추가 */
    public void addAdmin(AuthorityRequestDTO authorityRequestDTO) {
        // 권한 추가할 관리자 상세 정보 저장
        Authority authority = authorityRequestDTO.toAuthorityEntity();
        authorityRepository.save(authority);
    }

    /* 권한 취소 */
    public void deleteAdmin(Long id) {
        Authority authority = authorityRepository.findByAuthId(id)
                .orElseThrow(() -> new EntityNotFoundException("Authority with id " + id + " not found"));

        // 권한 취소할 관리자의 종료일시 저장
        authority.deleteAdmin(new Timestamp(System.currentTimeMillis()));
        authorityRepository.save(authority);
    }
}
