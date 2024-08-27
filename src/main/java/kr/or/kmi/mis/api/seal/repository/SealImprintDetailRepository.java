package kr.or.kmi.mis.api.seal.repository;

import kr.or.kmi.mis.api.seal.model.entity.SealImprintDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SealImprintDetailRepository extends JpaRepository<SealImprintDetail, Long> {
}
