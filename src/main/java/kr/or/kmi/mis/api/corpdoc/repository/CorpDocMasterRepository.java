package kr.or.kmi.mis.api.corpdoc.repository;

import kr.or.kmi.mis.api.corpdoc.model.entity.CorpDocMaster;
import org.springframework.data.jpa.repository.JpaRepository;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

public interface CorpDocMasterRepository extends JpaRepository<CorpDocMaster, Long> {
    Optional<List<CorpDocMaster>> findByDrafterIdAndStatus(String userId, String status);
    Optional<List<CorpDocMaster>> findByDrafterIdAndDraftDateBetween(String userId, Timestamp startDate, Timestamp endDate);
}
