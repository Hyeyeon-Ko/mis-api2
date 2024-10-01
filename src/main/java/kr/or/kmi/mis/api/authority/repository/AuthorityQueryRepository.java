package kr.or.kmi.mis.api.authority.repository;

import kr.or.kmi.mis.api.authority.model.response.AuthorityResponseDTO2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AuthorityQueryRepository {
    Page<AuthorityResponseDTO2> getAuthorityList(Pageable page);
}
