package kr.or.kmi.mis.api.rental.repository;

import kr.or.kmi.mis.api.rental.model.entity.RentalDetail;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RentalDetailRepository extends JpaRepository<RentalDetail, Long> {
    boolean existsByContractNum(String contractNum);
    Optional<RentalDetail> findByContractNum(String contractNum);
    Optional<List<RentalDetail>> findByInstCd(String instCd);
}
