package kr.or.kmi.mis.api.docstorage.repository;

import kr.or.kmi.mis.api.docstorage.domain.entity.DocStorageHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DocStorageHistoryRepository extends JpaRepository<DocStorageHistory, Long> {
}
