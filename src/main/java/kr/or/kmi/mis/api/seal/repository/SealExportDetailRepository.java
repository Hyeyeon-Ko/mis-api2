package kr.or.kmi.mis.api.seal.repository;

import kr.or.kmi.mis.api.seal.model.entity.SealExportDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SealExportDetailRepository extends JpaRepository<SealExportDetail, String> {
}
