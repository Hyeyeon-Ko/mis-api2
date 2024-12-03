package kr.or.kmi.mis.api.bcd.repository;

import kr.or.kmi.mis.api.bcd.model.entity.BcdMaster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface BcdMasterRepository extends JpaRepository<BcdMaster, String> {

    Optional<List<BcdMaster>> findByDrafterIdAndStatus(String drafterId, @Param("status") String status);

    Optional<List<BcdMaster>> findAllByStatusNotAndDraftDateBetweenOrderByDraftDateDesc(@Param("status") String status, LocalDateTime from, LocalDateTime to);

    List<BcdMaster> findAllByDraftIdIn(List<String> draftIds);

    Optional<BcdMaster> findTopByOrderByDraftIdDesc();

    Optional<BcdMaster> findByDraftIdAndStatusAndDrafterIdNot(String draftId, String status, String drafterId);

    Optional<List<BcdMaster>> findAllByStatusAndOrderDateIsNull(String status);
}
