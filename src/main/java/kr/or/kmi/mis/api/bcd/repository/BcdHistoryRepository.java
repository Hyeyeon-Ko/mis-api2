package kr.or.kmi.mis.api.bcd.repository;

import kr.or.kmi.mis.api.bcd.model.entity.BcdHistory;
import kr.or.kmi.mis.api.bcd.model.entity.DraftSeqPK;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BcdHistoryRepository extends JpaRepository<BcdHistory, DraftSeqPK> {



}
