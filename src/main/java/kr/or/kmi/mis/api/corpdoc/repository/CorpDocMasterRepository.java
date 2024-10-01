package kr.or.kmi.mis.api.corpdoc.repository;

import kr.or.kmi.mis.api.corpdoc.model.entity.CorpDocMaster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

@Repository
public interface CorpDocMasterRepository extends JpaRepository<CorpDocMaster, String> {

    Optional<List<CorpDocMaster>> findByDrafterIdAndStatus(String userId, String status);

    Optional<List<CorpDocMaster>> findByDrafterIdAndDraftDateBetween(String userId, Timestamp from, Timestamp to);

    Optional<CorpDocMaster> findTopByOrderByDraftIdDesc();

    List<CorpDocMaster> findAllByStatusNotAndDraftDateBetweenOrderByDraftDateDesc(String status, Timestamp from, Timestamp to);

    List<CorpDocMaster> findAllByStatusAndDraftDateBetweenOrderByDraftDateDesc(String status, Timestamp from, Timestamp to);

    List<CorpDocMaster> findAllByStatusOrderByDraftDateAsc(String status);

    List<CorpDocMaster> findAllByStatusAndInstCdOrderByEndDateAsc(String status, String instCd);
}
