package kr.or.kmi.mis.api.seal.repository;

import kr.or.kmi.mis.api.bcd.model.entity.DraftSeqPK;
import kr.or.kmi.mis.api.seal.model.entity.SealRegisterHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface SealRegisterHistoryRepository extends JpaRepository<SealRegisterHistory, DraftSeqPK> {

    Optional<SealRegisterHistory> findTopByDraftIdOrderBySeqIdDesc(@Param("draftId") String draftId);
}
