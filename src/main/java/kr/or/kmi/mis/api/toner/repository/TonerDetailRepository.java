package kr.or.kmi.mis.api.toner.repository;

import kr.or.kmi.mis.api.toner.model.entity.TonerDetail;
import kr.or.kmi.mis.api.toner.model.entity.TonerDetailId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TonerDetailRepository extends JpaRepository<TonerDetail, TonerDetailId> {
}
