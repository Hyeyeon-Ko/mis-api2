package kr.or.kmi.mis.api.toner.repository;

import kr.or.kmi.mis.api.toner.model.entity.TonerInfo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TonerInfoRepository extends JpaRepository<TonerInfo, String> {

    boolean existsByMngNum(String MngNum);
    List<TonerInfo> findAllByInstCd(String instCd);
    Optional<TonerInfo> findByMngNum(String mngNum);
}
