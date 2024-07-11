package kr.or.kmi.mis.api.bcd.repository;

import kr.or.kmi.mis.api.bcd.model.entity.BcdDetail;
import kr.or.kmi.mis.api.bcd.model.entity.DraftSeqPK;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

@Repository
public interface BcdDetailRepository extends JpaRepository<BcdDetail, DraftSeqPK> {

    Optional<BcdDetail> findByDraftId(Long draftId);

    Optional<BcdDetail> findTopByDraftIdOrderBySeqIdAsc(@Param("draftId") Long draftId);

    Optional<BcdDetail> findTopByDraftIdOrderBySeqIdDesc(@Param("draftId") Long draftId);

    List<Long> findDistinctDraftIdByUserId(String userId);

    List<Long> findDistinctDraftIdByUserIdAndDraftDateBetween(String userId, Timestamp startDate, Timestamp endDate);

    List<BcdDetail> findAllByDraftIdIn(List<Long> draftIds);
}
