package kr.or.kmi.mis.api.bcd.repository;

import kr.or.kmi.mis.api.bcd.model.entity.BcdDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BcdDetailRepository extends JpaRepository<BcdDetail, String> {

    List<BcdDetail> findAllByDraftIdIn(List<String> draftIds);

    List<BcdDetail> findAllByUserId(String userId);

}
