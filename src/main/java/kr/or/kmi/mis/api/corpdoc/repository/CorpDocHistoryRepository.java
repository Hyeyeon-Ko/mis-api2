package kr.or.kmi.mis.api.corpdoc.repository;

import kr.or.kmi.mis.api.bcd.model.entity.DraftSeqPK;
import kr.or.kmi.mis.api.corpdoc.model.entity.CorpDocHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CorpDocHistoryRepository extends JpaRepository<CorpDocHistory, DraftSeqPK> {
    Optional<CorpDocHistory> findTopByDraftIdOrderBySeqIdDesc(String draftId);
}
