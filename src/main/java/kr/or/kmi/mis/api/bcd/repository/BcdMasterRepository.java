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

    Optional<List<BcdMaster>> findByDrafterId(String drafterId);

    Optional<List<BcdMaster>> findAllByStatusNotOrderByDraftDateDesc(@Param("status") String status);

    List<BcdMaster> findAllByStatusOrderByDraftDateDesc(String status);

    Optional<BcdMaster> findByDraftIdAndStatusAndDrafterIdNot(Long draftId, String status, String drafterId);

    Optional<List<BcdMaster>> findAllByStatusAndOrderDateIsNull(String status);

    Optional<List<BcdMaster>> findByDraftIdAndDrafterIdNot(Long draftId, String drafterId);

    Optional<List<BcdMaster>> findAllByDrafterId(String drafterId);
}
