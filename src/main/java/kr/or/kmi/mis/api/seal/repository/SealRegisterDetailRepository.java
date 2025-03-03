package kr.or.kmi.mis.api.seal.repository;

import kr.or.kmi.mis.api.seal.model.entity.SealRegisterDetail;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SealRegisterDetailRepository extends JpaRepository<SealRegisterDetail, String> {

    List<SealRegisterDetail> findAllByInstCdAndDeletedtNull(String instCd);

    List<SealRegisterDetail> findAllByDeletedtNull();

    Optional<SealRegisterDetail> findTopByOrderByDraftIdDesc();
}
