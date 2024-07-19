package kr.or.kmi.mis.api.bcd.repository;

import kr.or.kmi.mis.api.bcd.model.entity.BcdHistory;
import kr.or.kmi.mis.api.bcd.model.entity.DraftSeqPK;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BcdHistoryRepository extends JpaRepository<BcdHistory, DraftSeqPK> {

    Optional<BcdHistory> findTopByDraftIdOrderBySeqIdDesc(@Param("draftId") Long draftId);

}
