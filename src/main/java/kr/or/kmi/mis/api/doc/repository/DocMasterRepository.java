package kr.or.kmi.mis.api.doc.repository;

import kr.or.kmi.mis.api.doc.model.entity.DocMaster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

@Repository
public interface DocMasterRepository extends JpaRepository<DocMaster, Long> {

    Optional<List<DocMaster>> findByDrafterIdAndDraftDateBetween(String userId, Timestamp startDate, Timestamp endDate);

    Optional<List<DocMaster>> findByDrafterIdAndStatus(String userId, String status);

    List<DocMaster> findAllByStatusNotAndDraftDateBetweenOrderByDraftDateDesc(String status, Timestamp startDate, Timestamp endDate);

    List<DocMaster> findAllByStatusOrderByDraftDateDesc(String status);
}
