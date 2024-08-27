package kr.or.kmi.mis.api.seal.repository;

import kr.or.kmi.mis.api.seal.model.entity.SealExportHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SealExportHistoryRepository extends JpaRepository<SealExportHistory, Long> {
}
