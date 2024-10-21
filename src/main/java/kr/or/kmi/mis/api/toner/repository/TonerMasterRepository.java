package kr.or.kmi.mis.api.toner.repository;

import kr.or.kmi.mis.api.corpdoc.model.entity.CorpDocMaster;
import kr.or.kmi.mis.api.toner.model.entity.TonerMaster;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TonerMasterRepository extends JpaRepository<TonerMaster, String> {

    Optional<TonerMaster> findTopByOrderByDraftIdDesc();
}
