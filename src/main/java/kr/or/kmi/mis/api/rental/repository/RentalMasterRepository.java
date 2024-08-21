package kr.or.kmi.mis.api.rental.repository;

import kr.or.kmi.mis.api.rental.model.entity.RentalMaster;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RentalMasterRepository extends JpaRepository<RentalMaster, Long> {
}
