package kr.or.kmi.mis.api.toner.repository;

import kr.or.kmi.mis.api.toner.model.entity.TonerDetail;
import kr.or.kmi.mis.api.toner.model.entity.TonerDetailId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TonerDetailRepository extends JpaRepository<TonerDetail, TonerDetailId> {

    Optional<TonerDetail> findByDraftIdAndItemId(String draftId, Long itemId);

    List<TonerDetail> findAllByDraftId(String draftId);

    List<TonerDetail> findAllByDraftIdIn(List<String> draftIds);
}
