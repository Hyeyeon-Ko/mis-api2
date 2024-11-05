package kr.or.kmi.mis.api.authority.service;

import kr.or.kmi.mis.api.authority.model.request.AuthorityRequestDTO;
import kr.or.kmi.mis.api.authority.model.response.AuthorityResponseDTO;
import kr.or.kmi.mis.api.authority.model.response.AuthorityResponseDTO2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AuthorityService {

    /* 해당 사번의 기준자료 권한 불러오기 */
    boolean hasStandardDataManagementAuthority();

    /* 해당 센터의 총무팀 목록 불러오기 */
    String getMemberName(String userId);

    /* 권한 추가 */
    void addAdmin(AuthorityRequestDTO stdDetailAuthorityRequestDTO);

    /*권한 수정*/
    void updateAdmin(Long authId, AuthorityRequestDTO request);

    /* 권한 취소 */
    void deleteAdmin(Long authId);

    /* 특정 관리자 상세정보 조회 */
    AuthorityResponseDTO getAdmin(Long authId);

    /* 모든 권한 목록 조회 */
    Page<AuthorityResponseDTO2> getAuthorityList2(Pageable page);
}
