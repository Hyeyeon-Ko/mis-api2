package kr.or.kmi.mis.api.std.repository;

import kr.or.kmi.mis.api.std.model.entity.StdDetail;
import kr.or.kmi.mis.api.std.model.entity.StdGroup;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface StdDetailRepository extends JpaRepository<StdDetail, String> {

    public Optional<List<StdDetail>> findAllByUseAtNotAndGroupCd(String useAt, StdGroup groupCd);

    Optional<StdDetail> findByDetailCd(String instCd);

    Optional<StdDetail> findByEtcItem1(String userId);

    Optional<StdDetail> findByGroupCdAndDetailNm(StdGroup groupCd, String detailNm);
}
