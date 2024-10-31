package kr.or.kmi.mis.api.file.repository;

import kr.or.kmi.mis.api.file.model.entity.FileHistory;
import kr.or.kmi.mis.api.file.model.entity.IdSeqPK;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FileHistoryRepository extends JpaRepository<FileHistory, IdSeqPK> {
    Optional<FileHistory> findTopByAttachIdOrderBySeqIdDesc(String attachId);
}
