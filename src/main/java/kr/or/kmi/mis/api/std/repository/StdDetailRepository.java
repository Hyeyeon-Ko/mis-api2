package kr.or.kmi.mis.api.std.repository;

import kr.or.kmi.mis.api.std.model.entity.StdClass;
import kr.or.kmi.mis.api.std.model.entity.StdDetail;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StdDetailRepository extends JpaRepository<StdDetail, String> {

}
