package kr.or.kmi.mis.api.std.repository;

import kr.or.kmi.mis.api.std.model.entity.StdClass;
import kr.or.kmi.mis.api.std.model.entity.StdDetailHist;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StdDetailHistRepository extends JpaRepository<StdDetailHist, Long> {

}
