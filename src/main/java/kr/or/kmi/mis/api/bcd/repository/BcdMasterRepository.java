package kr.or.kmi.mis.api.bcd.repository;

import kr.or.kmi.mis.api.bcd.model.entity.BcdMaster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface BcdMasterRepository extends JpaRepository<BcdMaster, String> {

    Optional<List<BcdMaster>> findByDrafterIdAndStatusAndCurrentApproverIndex(String drafterId, @Param("status") String status, Integer approverIndex);

    Optional<List<BcdMaster>> findByDrafterIdAndDraftDateBetween(String drafterId, Timestamp from, Timestamp to);

    Optional<List<BcdMaster>> findAllByStatusNotAndDraftDateBetweenOrderByDraftDateDesc(@Param("status") String status, Timestamp from, Timestamp to);

    List<BcdMaster> findAllByStatusAndDraftDateBetweenOrderByDraftDateDesc(String status, Timestamp from, Timestamp to);

    Optional<BcdMaster> findByDraftIdAndStatusAndDrafterIdNot(String draftId, String status, String drafterId);

    Optional<BcdMaster> findTopByOrderByDraftIdDesc();

    Optional<BcdMaster> findByDraftIdAndStatusAndCurrentApproverIndexAndDrafterIdNot(String draftId, String status, Integer approverIndex, String drafterId);

    Optional<List<BcdMaster>> findAllByStatusAndCurrentApproverIndex(String status, Integer approverIndex);

    Optional<List<BcdMaster>> findAllByStatusAndOrderDateIsNull(String status);

    Optional<List<BcdMaster>> findByDraftIdAndDraftDateBetweenAndDrafterIdNot(String draftId, Timestamp from, Timestamp to, String drafterId);

    Optional<List<BcdMaster>> findAllByDrafterId(String drafterId);

    Optional<List<BcdMaster>> findAllByDrafterIdAndDraftDateBetweenOrderByDraftDateDesc(String drafterId, Timestamp from, Timestamp to);
}
