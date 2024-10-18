package kr.or.kmi.mis.api.corpdoc.repository;

import kr.or.kmi.mis.api.corpdoc.model.entity.CorpDocMaster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface CorpDocMasterRepository extends JpaRepository<CorpDocMaster, String> {

    Optional<List<CorpDocMaster>> findByDrafterIdAndStatus(String userId, String status);

    Optional<List<CorpDocMaster>> findByDrafterIdAndDraftDateBetween(String userId, LocalDateTime from, LocalDateTime to);

    List<CorpDocMaster> findAllByStatusNotAndDraftDateBetweenOrderByDraftDateDesc(String status, LocalDateTime from, LocalDateTime to);
    Optional<CorpDocMaster> findTopByOrderByDraftIdDesc();

    List<CorpDocMaster> findAllByStatusAndDraftDateBetweenOrderByDraftDateDesc(String status, LocalDateTime from, LocalDateTime to);

    List<CorpDocMaster> findAllByStatusOrderByDraftDateAsc(String status);

    List<CorpDocMaster> findAllByStatusAndInstCdOrderByEndDateAsc(String status, String instCd);
}
