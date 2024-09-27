package kr.or.kmi.mis.api.file.repository;

import kr.or.kmi.mis.api.file.model.entity.FileHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FileHistoryRepository extends JpaRepository<FileHistory, Long> {
    Optional<FileHistory> findTopByIdAndDocTypeOrderBySeqIdDesc(Long id, String docType);
}
