package kr.or.kmi.mis.api.toner.repository;

import kr.or.kmi.mis.api.toner.model.entity.TonerInfo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TonerInfoRepository extends JpaRepository<TonerInfo, String> {

    List<TonerInfo> findAllByInstCd(String instCd);
    Optional<TonerInfo> findByModelNmAndTonerNm(String modelNm, String tonerNm);
    Optional<TonerInfo> findFirstByTonerNm(String tonerNm);
    Optional<List<TonerInfo>> findAllByTonerNm(String tonerNm);
}
