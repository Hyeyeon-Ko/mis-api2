package kr.or.kmi.mis.api.docstorage.repository;

import kr.or.kmi.mis.api.docstorage.domain.entity.DocStorageMaster;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DocStorageMasterRepository extends JpaRepository<DocStorageMaster, Long> {
}
