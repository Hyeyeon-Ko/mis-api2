package kr.or.kmi.mis.api.doc.repository;

import kr.or.kmi.mis.api.bcd.model.entity.DraftSeqPK;
import kr.or.kmi.mis.api.doc.model.entity.DocHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DocHistoryRepository extends JpaRepository<DocHistory, DraftSeqPK> {

    Optional<DocHistory> findTopByDraftIdOrderBySeqIdDesc(@Param("draftId") String draftId);

}
