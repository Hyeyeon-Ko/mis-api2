package kr.or.kmi.mis.api.std.repository.impl;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import kr.or.kmi.mis.api.std.model.entity.QStdDetail;
import kr.or.kmi.mis.api.std.model.response.StdDetailResponseDTO;
import kr.or.kmi.mis.api.std.repository.StdDetailQueryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * packageName    : kr.or.kmi.mis.api.std.repository.impl
 * fileName       : StdDetailQueryRepositoryImpl
 * author         : KMI_DI
 * date           : 2024-10-01
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2024-10-01        KMI_DI       the first create
 */
@Repository
@RequiredArgsConstructor
public class StdDetailQueryRepositoryImpl implements StdDetailQueryRepository {

    private final JPAQueryFactory queryFactory;
    private final QStdDetail stdDetail = QStdDetail.stdDetail;

    @Override
    public Page<StdDetailResponseDTO> getInfo2(String groupCd, Pageable page) {
        List<StdDetailResponseDTO> resultSet = queryFactory.select(
                    Projections.constructor(
                            StdDetailResponseDTO.class,
                            stdDetail.detailCd,
                            stdDetail.groupCd.groupCd,
                            stdDetail.detailNm,
                            stdDetail.fromDd,
                            stdDetail.toDd,
                            stdDetail.etcItem1,
                            stdDetail.etcItem2,
                            stdDetail.etcItem3,
                            stdDetail.etcItem4,
                            stdDetail.etcItem5,
                            stdDetail.etcItem6,
                            stdDetail.etcItem7,
                            stdDetail.etcItem8,
                            stdDetail.etcItem9,
                            stdDetail.etcItem10,
                            stdDetail.etcItem11
                    )
                )
                .from(stdDetail)
                .where(
                        stdDetail.groupCd.groupCd.eq(groupCd),
                        stdDetail.useAt.eq("Y")
                )
                .orderBy(stdDetail.detailCd.asc())
                .offset(page.getOffset())
                .limit(page.getPageSize())
                .fetch()
                ;

        Long count = queryFactory.select(stdDetail.count())
                .from(stdDetail)
                .where(
                        stdDetail.groupCd.groupCd.eq(groupCd),
                        stdDetail.useAt.eq("Y")
                )
                .fetchOne();

        return new PageImpl<>(resultSet, page, count);
    }

    @Override
    public String findDetailCd(String teamCd, String instCd) {

        // (1) group_cd가 'A003'이고, etc_item3가 g_team_cd인 칼럼들 중에서 etc_item1을 가져오는 서브쿼리
        List<String> etcItem1List = queryFactory
                .select(stdDetail.etcItem1)
                .from(stdDetail)
                .where(stdDetail.groupCd.groupCd.eq("A003")
                        .and(stdDetail.etcItem3.eq(teamCd)))
                .fetch();

        // (2) group_cd가 'A002'이고, etc_item1이 user_inst_cd인 칼럼들 중에서 detail_cd를 가져오는 쿼리
        return queryFactory
                .select(stdDetail.detailCd)
                .from(stdDetail)
                .where(stdDetail.groupCd.groupCd.eq("A002")
                        .and(stdDetail.etcItem1.eq(instCd))
                        .and(stdDetail.detailCd.in(etcItem1List)))  // (1)의 etc_item1과 (2)의 detail_cd가 같은지 비교
                .fetchOne();
    }


}
