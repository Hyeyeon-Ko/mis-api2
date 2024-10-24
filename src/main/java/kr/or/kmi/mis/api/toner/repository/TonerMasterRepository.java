package kr.or.kmi.mis.api.toner.repository;

import kr.or.kmi.mis.api.toner.model.entity.TonerMaster;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TonerMasterRepository extends JpaRepository<TonerMaster, String> {

    Optional<TonerMaster> findTopByOrderByDraftIdDesc();

    Optional<List<TonerMaster>> findAllByStatusAndInstCd(String status, String instCd);

    Optional<List<TonerMaster>> findAllByDraftIdIn(List<String> draftIds);

    Optional<List<TonerMaster>> findByDrafterIdAndStatus(String userId, String status);
}
