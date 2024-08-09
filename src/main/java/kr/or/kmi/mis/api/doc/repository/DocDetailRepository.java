package kr.or.kmi.mis.api.doc.repository;

import kr.or.kmi.mis.api.doc.model.entity.DocDetail;
import kr.or.kmi.mis.api.doc.model.entity.DocMaster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DocDetailRepository extends JpaRepository<DocDetail, Long> {

    Optional<DocDetail> findFirstByDocIdNotNullAndDivisionOrderByDocIdDesc(String division);
    List<DocDetail> findAllByDocIdNotNullAndDivision(String division);
}