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

    Optional<List<BcdMaster>> findByDrafterIdAndStatusOrderByDraftDateDesc(String drafterId, @Param("status") String status);

    Optional<List<BcdMaster>> findByDrafterIdAndDraftDateBetweenOrderByDraftDateDesc(String drafterId, Timestamp startDate, Timestamp endDate);

    Optional<List<BcdMaster>> findAllByStatusNotAndDraftDateBetweenOrderByDraftDateDesc(@Param("status") String status, Timestamp startDate, Timestamp endDate);

    Optional<String> findDrafterByDraftId(Long id);
    BcdMaster findByDraftId(Long id);
    List<BcdMaster> findAllByStatus(String status);
}
