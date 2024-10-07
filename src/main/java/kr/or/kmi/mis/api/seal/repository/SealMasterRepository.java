package kr.or.kmi.mis.api.seal.repository;

import kr.or.kmi.mis.api.seal.model.entity.SealMaster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface SealMasterRepository extends JpaRepository<SealMaster, String> {

    Optional<List<SealMaster>> findAllByStatusAndDivisionAndInstCd(String status, String division, String instCd);

    Optional<List<SealMaster>> findByDrafterIdAndDraftDateBetween(String userId, LocalDateTime from, LocalDateTime to);

    Optional<List<SealMaster>> findByDrafterIdAndStatus(String userId, String status);

    Optional<SealMaster> findTopByOrderByDraftIdDesc();

    List<SealMaster> findAllByStatusNotAndInstCdAndDraftDateBetweenOrderByDraftDateDesc(String status, String instCd, LocalDateTime from, LocalDateTime to);

    List<SealMaster> findAllByStatusAndInstCdAndDraftDateBetweenOrderByDraftDateDesc(String status, String instCd, LocalDateTime from, LocalDateTime to);

}
