package kr.or.kmi.mis.api.rental.repository;

import kr.or.kmi.mis.api.rental.model.entity.RentalDetail;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RentalDetailRepository extends JpaRepository<RentalDetail, Long> {
}
