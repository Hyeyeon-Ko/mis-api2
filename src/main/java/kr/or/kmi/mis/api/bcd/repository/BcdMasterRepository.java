package kr.or.kmi.mis.api.bcd.repository;

import kr.or.kmi.mis.api.bcd.model.entity.BcdMaster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

@Repository
public interface BcdMasterRepository extends JpaRepository<BcdMaster, Long> {

    Optional<List<BcdMaster>> findByDrafterIdAndStatus(String drafterId, @Param("status") String status);

    Optional<List<BcdMaster>> findByDrafterIdAndDraftDateBetween(String drafterId, Timestamp startDate, Timestamp endDate);

    Optional<List<BcdMaster>> findAllByStatusNotAndDraftDateBetweenOrderByDraftDateDesc(@Param("status") String status, Timestamp startDate, Timestamp endDate);

    List<BcdMaster> findAllByStatusOrderByDraftDateDesc(String status);

    Optional<BcdMaster> findByDraftIdAndStatus(Long draftId, String status);

    Optional<BcdMaster> findByDraftId(Long id);

    Optional<List<BcdMaster>> findAllByStatus(String status);

    Optional<List<BcdMaster>> findByDraftIdAndDraftDateBetween(Long draftId, Timestamp startDate, Timestamp endDate);
}
