package kr.or.kmi.mis.api.file.repository;

import kr.or.kmi.mis.api.file.model.entity.FileDetail;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FileDetailRepository extends JpaRepository<FileDetail, Long> {

    Optional<FileDetail> findByDraftIdAndDocType(Long draftId, String docType);
}
