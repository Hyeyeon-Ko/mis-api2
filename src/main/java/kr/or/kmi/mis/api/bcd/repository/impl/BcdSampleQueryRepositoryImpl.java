package kr.or.kmi.mis.api.bcd.repository.impl;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.StringTemplate;
import com.querydsl.jpa.impl.JPAQueryFactory;
import kr.or.kmi.mis.api.bcd.model.entity.QBcdDetail;
import kr.or.kmi.mis.api.bcd.model.response.BcdSampleResponseDTO;
import kr.or.kmi.mis.api.bcd.repository.BcdSampleQueryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

/**
 * packageName    : kr.or.kmi.mis.api.bcd.repository.impl
 * fileName       : BcdSampleQueryRepositoryImpl
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
public class BcdSampleQueryRepositoryImpl implements BcdSampleQueryRepository {
    private final JPAQueryFactory queryFactory;
    private final QBcdDetail qBcdDetail = QBcdDetail.bcdDetail;

    @Override
    public BcdSampleResponseDTO getBcdSampleNm(String groupCd, String detailCd) {

        StringTemplate instNm = Expressions.stringTemplate("function('fn_getCodeNm', {0}, {1})", groupCd, detailCd);
        String draftId = "1";

        return queryFactory.select(
                        Projections.constructor(
                                BcdSampleResponseDTO.class,
                                qBcdDetail.instCd,
                                instNm.as("instNm")
                        )
                )
                .from(qBcdDetail)
                .where(
                        qBcdDetail.draftId.eq(draftId)
                )
                .fetchOne();
    }
}
