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

    Optional<List<SealMaster>> findByDrafterIdAndDraftDateBetween(String userId, Timestamp startDate, Timestamp endDate);

    Optional<List<SealMaster>> findByDrafterIdAndStatus(String userId, String status);

    List<SealMaster> findAllByStatusNotAndDraftDateBetweenAndInstCdOrderByDraftDateDesc(String status, Timestamp startDate, Timestamp endDate, String instCd);

    List<SealMaster> findAllByStatusAndInstCdOrderByDraftDateDesc(String status, String instCd);

}
