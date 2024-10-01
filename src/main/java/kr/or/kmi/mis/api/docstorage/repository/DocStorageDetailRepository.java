package kr.or.kmi.mis.api.docstorage.repository;

import kr.or.kmi.mis.api.docstorage.domain.entity.DocStorageDetail;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DocStorageDetailRepository extends JpaRepository<DocStorageDetail, Long> {

    Optional<List<DocStorageDetail>> findAllByDraftId(String draftId);
    Optional<List<DocStorageDetail>> findAllByDeptCd(String deptCd);
    Optional<List<DocStorageDetail>> findByDeptCdAndStatus(String detailCd, String status);
    List<DocStorageDetail> findAllByDetailIdIn(List<Long> detailIds);
    Optional<DocStorageDetail> findByDocId(String DocId);
    boolean existsByDocId(String docId);
}
