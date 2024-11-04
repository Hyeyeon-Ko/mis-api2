package kr.or.kmi.mis.api.toner.repository;

import kr.or.kmi.mis.api.toner.model.entity.TonerPrice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TonerPriceRepository extends JpaRepository<TonerPrice, String> {

    boolean existsByTonerNm(String TonerNum);
    Optional<TonerPrice> findByTonerNm(String tonerNm);
    List<TonerPrice> findAllByTonerNmIn(List<String> tonerNms);
}
