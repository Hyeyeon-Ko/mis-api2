package kr.or.kmi.mis.api.docstorage.repository;

import kr.or.kmi.mis.api.docstorage.domain.entity.DocStorageMaster;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DocStorageMasterRepository extends JpaRepository<DocStorageMaster, Long> {

    Optional<List<DocStorageMaster>> findAllByInstCd(String instCd);
}
