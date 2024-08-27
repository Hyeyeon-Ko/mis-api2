package kr.or.kmi.mis.api.doc.repository;

import kr.or.kmi.mis.api.doc.model.entity.DocMaster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

@Repository
public interface DocMasterRepository extends JpaRepository<DocMaster, Long> {

    Optional<DocMaster> findByDraftIdAndInstCd(Long draftId, String instCd);

    Optional<List<DocMaster>> findByDrafterIdAndDraftDateBetween(String userId, Timestamp startDate, Timestamp endDate);

    Optional<List<DocMaster>> findByDrafterIdAndStatus(String userId, String status);

    List<DocMaster> findAllByDeptCd(String deptCd);

    List<DocMaster> findAllByStatusNotAndDraftDateBetweenAndInstCdOrderByDraftDateDesc(String status, Timestamp startDate, Timestamp endDate, String instCd);

    List<DocMaster> findAllByStatusAndInstCdOrderByDraftDateDesc(String status, String instCd);
}
