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

    Optional<List<DocMaster>> findByDrafterId(String userId);

    Optional<List<DocMaster>> findByDrafterIdAndStatusAndCurrentApproverIndex(String userId, String status, Integer approverIndex);

    Optional<List<DocMaster>> findAllByStatusAndCurrentApproverIndex(String status, Integer approverIndex);

    List<DocMaster> findAllByDeptCd(String deptCd);

    List<DocMaster> findAllByStatusNotAndInstCdOrderByDraftDateDesc(String status, String instCd);

    List<DocMaster> findAllByStatusAndInstCdOrderByDraftDateDesc(String status, String instCd);
}
