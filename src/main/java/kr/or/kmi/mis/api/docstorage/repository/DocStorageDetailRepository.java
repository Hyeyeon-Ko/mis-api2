package kr.or.kmi.mis.api.docstorage.repository;

import kr.or.kmi.mis.api.docstorage.domain.entity.DocStorageDetail;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DocStorageDetailRepository extends JpaRepository<DocStorageDetail, Long> {
}
