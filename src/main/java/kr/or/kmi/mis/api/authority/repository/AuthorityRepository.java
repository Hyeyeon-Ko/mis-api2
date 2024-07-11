package kr.or.kmi.mis.api.authority.repository;


import kr.or.kmi.mis.api.authority.model.entity.Authority;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AuthorityRepository extends JpaRepository<Authority, Long> {

    List<Authority> findAllByDeletedtIsNull();

    Optional<Authority> findByAuthId(Long authId);

    Optional<Authority> findByUserId(String userId);
}
