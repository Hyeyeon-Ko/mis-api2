package kr.or.kmi.mis.api.seal.repository;

import kr.or.kmi.mis.api.seal.model.entity.SealImprintHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SealImprintHistoryRepository extends JpaRepository<SealImprintHistory, Long> {

    Optional<SealImprintHistory> findTopByDraftIdOrderBySeqIdDesc(@Param("draftId") Long draftId);
}
