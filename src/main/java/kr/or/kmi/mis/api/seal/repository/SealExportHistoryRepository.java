package kr.or.kmi.mis.api.seal.repository;

import kr.or.kmi.mis.api.bcd.model.entity.DraftSeqPK;
import kr.or.kmi.mis.api.seal.model.entity.SealExportHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SealExportHistoryRepository extends JpaRepository<SealExportHistory, DraftSeqPK> {

    Optional<SealExportHistory> findTopByDraftIdOrderBySeqIdDesc(@Param("draftId") String draftId);
}
