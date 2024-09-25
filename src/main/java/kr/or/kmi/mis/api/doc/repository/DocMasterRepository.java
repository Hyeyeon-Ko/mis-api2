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

    Optional<DocMaster> findByDraftIdAndInstCdAndDraftDateBetweenOrderByDraftDateDesc(Long draftId, String instCd, Timestamp from, Timestamp to);

    Optional<List<DocMaster>> findByDrafterIdAndDraftDateBetween(String userId, Timestamp from, Timestamp to);

    Optional<List<DocMaster>> findByDrafterIdAndStatusAndCurrentApproverIndex(String userId, String status, Integer approverIndex);

    Optional<List<DocMaster>> findAllByStatusAndCurrentApproverIndex(String status, Integer approverIndex);

    List<DocMaster> findAllByDeptCd(String deptCd);

    List<DocMaster> findAllByStatusNotAndInstCdAndDraftDateBetweenOrderByDraftDateDesc(String status, String instCd, Timestamp from, Timestamp to);

    List<DocMaster> findAllByStatusAndInstCdAndDraftDateBetweenOrderByDraftDateDesc(String status, String instCd, Timestamp from, Timestamp to);
}
