package kr.or.kmi.mis.api.std.repository;

import kr.or.kmi.mis.api.std.model.entity.StdDetail;
import kr.or.kmi.mis.api.std.model.entity.StdDetailId;
import kr.or.kmi.mis.api.std.model.entity.StdGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StdDetailRepository extends JpaRepository<StdDetail, StdDetailId> {

    Optional<List<StdDetail>> findAllByUseAtAndGroupCd(String useAt, StdGroup groupCd);

    Optional<StdDetail> findByDetailCd(String detailCd);

    Optional<StdDetail> findByEtcItem1(String userId);

    Optional<StdDetail> findByGroupCdAndDetailNm(StdGroup groupCd, String detailNm);

    Optional<StdDetail> findByGroupCdAndDetailCd(StdGroup groupCd, String detailCd);

    Optional<List<StdDetail>> findByGroupCd(StdGroup applyStatus);

    void deleteByGroupCdAndDetailCd(StdGroup stdGroup, String detailCd);

    Optional<List<StdDetail>> findByUseAtAndGroupCdAndDetailCd(String useAt, StdGroup groupCd,  String detailCd);
}
