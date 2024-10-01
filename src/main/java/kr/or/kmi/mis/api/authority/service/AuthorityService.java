package kr.or.kmi.mis.api.authority.service;

import kr.or.kmi.mis.api.authority.model.request.AuthorityRequestDTO;
//import kr.or.kmi.mis.api.authority.model.response.AuthorityListResponseDTO;
import kr.or.kmi.mis.api.authority.model.response.AuthorityResponseDTO;
import kr.or.kmi.mis.api.authority.model.response.AuthorityResponseDTO2;
import kr.or.kmi.mis.api.authority.model.response.ResponseData;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Mono;

import java.util.List;

public interface AuthorityService {

    /* 관리자 목록 불러오기 */
//    List<AuthorityListResponseDTO> getAuthorityList();

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

    /* 외부 사용자 정보 API 에서 사용자 정보 가져오기 */
    Mono<ResponseData.ResultData> fetchUserInfo(String userId);

    AuthorityResponseDTO getAdmin(Long authId);

    Page<AuthorityResponseDTO2> getAuthorityList2(Pageable page);
}
