package kr.or.kmi.mis.api.file.repository;

import kr.or.kmi.mis.api.file.model.entity.FileDownloadHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FileDownloadHistoryRepository extends JpaRepository<FileDownloadHistory, Long> {
}
