package kr.or.kmi.mis.api.std.repository;

import kr.or.kmi.mis.api.std.model.entity.StdClass;
import kr.or.kmi.mis.api.std.model.entity.StdGroup;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface StdGroupRepository extends JpaRepository<StdGroup, String> {

    Optional<List<StdGroup>> findAllByClassCd(StdClass classCd);
}
