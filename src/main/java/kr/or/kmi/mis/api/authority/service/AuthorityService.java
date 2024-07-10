package kr.or.kmi.mis.api.authority.service;

import kr.or.kmi.mis.api.authority.model.response.AuthorityListResponseDTO;
import kr.or.kmi.mis.api.authority.model.response.MemberListResponseDTO;

import java.util.List;

public interface AuthorityService {

    /* 관리자 목록 불러오기 */
    List<AuthorityListResponseDTO> getAuthorityList();

    /* 해당 센터의 총무팀 목록 불러오기 */
    List<MemberListResponseDTO> getMemberList(String center);

    /* 권한 추가 */
    void addAdmin(String instCd, String userId, String userRole);

    /* 권한 취소 */
    void deleteAdmin(Long id);
}
