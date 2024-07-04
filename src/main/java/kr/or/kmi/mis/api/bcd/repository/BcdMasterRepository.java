package kr.or.kmi.mis.api.bcd.repository;

import kr.or.kmi.mis.api.bcd.model.entity.BcdMaster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BcdMasterRepository extends JpaRepository<BcdMaster, Long> {

    Optional<List<BcdMaster>> findByDrafterIdOrderByDraftDateDesc(String drafterId);

    @Query("SELECT b FROM BcdMaster b WHERE b.status <> :status")
    Optional<List<BcdMaster>> findAllByStatusNotOrderByDraftDateDesc(@Param("status") String status);

}
