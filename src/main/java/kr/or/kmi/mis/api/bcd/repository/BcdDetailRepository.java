package kr.or.kmi.mis.api.bcd.repository;

import kr.or.kmi.mis.api.bcd.model.entity.BcdDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BcdDetailRepository extends JpaRepository<BcdDetail, Long> {

    Optional<BcdDetail> findByDraftId(Long draftId);

    @Query("SELECT b FROM BcdDetail b WHERE b.draftId = :draftId ORDER BY b.seqId ASC")
    Optional<BcdDetail> findFirstByDraftIdOrderBySeqIdAsc(@Param("draftId") Long draftId);

    @Query("SELECT DISTINCT b.draftId FROM BcdDetail b WHERE b.userId = :userId")
    List<Long> findDistinctDraftIdByUserId(String userId);

    Optional<BcdDetail> findByDraftIdAndSeqId(Long draftId, Long seqId);
}
