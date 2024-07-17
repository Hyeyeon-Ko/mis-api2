package kr.or.kmi.mis.api.std.repository.impl;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.transaction.Transactional;
import kr.or.kmi.mis.api.std.model.entity.QStdDetail;
import kr.or.kmi.mis.api.std.repository.StdClassQueryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

/**
 * packageName    : kr.or.kmi.mis.api.std.repository.impl
 * fileName       : StdClassQueryRepositoryImpl
 * author         : KMI_DI
 * date           : 2024-07-17
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2024-07-17        KMI_DI       the first create
 */
@Repository
@RequiredArgsConstructor
@Slf4j
public class StdClassQueryRepositoryImpl implements StdClassQueryRepository {
    private final JPAQueryFactory queryFactory;
    private final QStdDetail qStdDetail = QStdDetail.stdDetail;



    @Override
    @Transactional
    public String sample(String code) {
        return queryFactory.select(
                Projections.constructor(
                        String.class,
                        qStdDetail.detailNm
                    )
                )
                .from(qStdDetail)
                .fetchOne();
    }
}
