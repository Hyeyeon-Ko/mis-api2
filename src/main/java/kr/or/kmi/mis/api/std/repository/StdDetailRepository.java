package kr.or.kmi.mis.api.std.repository;

import kr.or.kmi.mis.api.std.model.entity.StdClass;
import kr.or.kmi.mis.api.std.model.entity.StdDetail;
import kr.or.kmi.mis.api.std.model.entity.StdGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface StdDetailRepository extends JpaRepository<StdDetail, String> {

    public Optional<List<StdDetail>> findAllByUseAtNotAndGroupCd(String useAt, StdGroup groupCd);

}
