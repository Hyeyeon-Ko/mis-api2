package kr.or.kmi.mis.api.bcd.repository;

import kr.or.kmi.mis.api.bcd.model.entity.BcdDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

@Repository
public interface BcdDetailRepository extends JpaRepository<BcdDetail, Long> {

    Optional<BcdDetail> findByDraftId(Long draftId);

    List<BcdDetail> findAllByDraftIdIn(List<Long> draftIds);

    List<BcdDetail> findAllByUserId(String userId);

}
