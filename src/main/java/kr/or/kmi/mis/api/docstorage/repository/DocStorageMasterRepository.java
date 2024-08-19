package kr.or.kmi.mis.api.docstorage.repository;

import kr.or.kmi.mis.api.docstorage.domain.entity.DocStorageMaster;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DocStorageMasterRepository extends JpaRepository<DocStorageMaster, Long> {

    Optional<List<DocStorageMaster>> findAllByInstCdAndStatus(String instCd, String status);
    Optional<List<DocStorageMaster>> findAllByInstCdAndType(String instCd, String type);
    Optional<List<DocStorageMaster>> findAllByInstCdAndStatusAndType(String instCd, String status, String type);

}
