package kr.or.kmi.mis.api.corpdoc.repository;

import kr.or.kmi.mis.api.corpdoc.model.entity.CorpDocMaster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

@Repository
public interface CorpDocMasterRepository extends JpaRepository<CorpDocMaster, Long> {

    Optional<List<CorpDocMaster>> findByDrafterIdAndStatus(String userId, String status);

    Optional<List<CorpDocMaster>> findByDrafterId(String userId);

    List<CorpDocMaster> findAllByStatusNotOrderByDraftDateDesc(String status);

    List<CorpDocMaster> findAllByStatusOrderByDraftDateDesc(String status);

    List<CorpDocMaster> findAllByStatusOrderByDraftDateAsc(String status);

    List<CorpDocMaster> findAllByStatusAndInstCdOrderByEndDateAsc(String status, String instCd);
}
