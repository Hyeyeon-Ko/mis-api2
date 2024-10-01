package kr.or.kmi.mis.api.std.repository.impl;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import kr.or.kmi.mis.api.std.model.entity.QStdDetail;
import kr.or.kmi.mis.api.std.model.response.StdDetailResponseDTO;
import kr.or.kmi.mis.api.std.repository.StdDetailQueryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

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
@Slf4j
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
                .orderBy(stdDetail.rgstDt.desc())
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

}
