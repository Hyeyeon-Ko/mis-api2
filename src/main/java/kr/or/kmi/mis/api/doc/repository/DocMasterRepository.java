package kr.or.kmi.mis.api.doc.repository;

import kr.or.kmi.mis.api.doc.model.entity.DocMaster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface DocMasterRepository extends JpaRepository<DocMaster, String> {

    Optional<DocMaster> findByDraftIdAndInstCd(String draftId, String instCd);

    Optional<DocMaster> findByDraftIdAndInstCdAndDraftDateBetweenOrderByDraftDateDesc(String draftId, String instCd, LocalDateTime from, LocalDateTime to);

    Optional<DocMaster> findTopByOrderByDraftIdDesc();

    Optional<List<DocMaster>> findByDrafterIdAndDraftDateBetween(String userId, LocalDateTime from, LocalDateTime to);

    Optional<List<DocMaster>> findByDrafterIdAndStatusAndCurrentApproverIndex(String userId, String status, Integer approverIndex);

    Optional<List<DocMaster>> findAllByStatusAndCurrentApproverIndex(String status, Integer approverIndex);

    List<DocMaster> findAllByDeptCd(String deptCd);

    List<DocMaster> findAllByStatusNotAndInstCdAndDraftDateBetweenOrderByDraftDateDesc(String status, String instCd, LocalDateTime from, LocalDateTime to);

    List<DocMaster> findAllByStatusAndInstCdAndDraftDateBetweenOrderByDraftDateDesc(String status, String instCd, LocalDateTime from, LocalDateTime to);
}
