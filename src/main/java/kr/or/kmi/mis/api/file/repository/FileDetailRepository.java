package kr.or.kmi.mis.api.file.repository;

import kr.or.kmi.mis.api.file.model.entity.FileDetail;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface FileDetailRepository extends JpaRepository<FileDetail, Long> {

    Optional<FileDetail> findByDraftId(String draftId);
    Optional<FileDetail> findTopByOrderByAttachIdDesc();
    List<FileDetail> findAllByDraftIdIn(Collection<String> draftId);

}
