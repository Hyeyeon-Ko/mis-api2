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

    Optional<StdDetail> findByDetailCdAndEtcItem1(String detailCd, String etcItem1);

    Optional<StdDetail> findByEtcItem1(String userId);

    Optional<List<StdDetail>> findByGroupCdAndDetailNm(StdGroup groupCd, String detailNm);

    Optional<StdDetail> findByGroupCdAndDetailCd(StdGroup groupCd, String detailCd);

    Optional<StdDetail> findByGroupCdAndDetailCdAndEtcItem1(StdGroup groupCd, String detailCd, String etcItem1);

    Optional<List<StdDetail>> findByGroupCdAndEtcItem3(StdGroup groupCd, String etcItem3);

    Optional<List<StdDetail>> findByGroupCdAndEtcItem1(StdGroup groupCd, String etcItem1);

    Optional<List<StdDetail>> findByGroupCd(StdGroup applyStatus);

    void deleteByGroupCdAndDetailCd(StdGroup stdGroup, String detailCd);

    Optional<List<StdDetail>> findByUseAtAndGroupCdAndDetailCd(String useAt, StdGroup groupCd,  String detailCd);

    Boolean existsByGroupCdAndDetailCd(StdGroup groupCd, String detailCd);
}
