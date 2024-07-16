package kr.or.kmi.mis.api.std.repository;

import kr.or.kmi.mis.api.std.model.entity.StdClass;
import kr.or.kmi.mis.api.std.model.entity.StdGroup;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StdGroupRepository extends JpaRepository<StdGroup, String> {

}
