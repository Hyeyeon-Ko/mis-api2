package kr.or.kmi.mis.api.seal.repository;

import kr.or.kmi.mis.api.seal.model.entity.SealMaster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

@Repository
public interface SealMasterRepository extends JpaRepository<SealMaster, Long> {

    Optional<List<SealMaster>> findAllByStatusAndDivisionAndInstCd(String status, String division, String instCd);

    Optional<List<SealMaster>> findByDrafterIdAndDraftDateBetween(String userId, Timestamp from, Timestamp to);

    Optional<List<SealMaster>> findByDrafterIdAndStatus(String userId, String status);

    List<SealMaster> findAllByStatusNotAndInstCdAndDraftDateBetweenOrderByDraftDateDesc(String status, String instCd, Timestamp from, Timestamp to);

    List<SealMaster> findAllByStatusAndInstCdAndDraftDateBetweenOrderByDraftDateDesc(String status, String instCd, Timestamp from, Timestamp to);

}
