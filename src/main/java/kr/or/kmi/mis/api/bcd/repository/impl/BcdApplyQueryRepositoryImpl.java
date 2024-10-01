package kr.or.kmi.mis.api.bcd.repository.impl;

import com.querydsl.core.types.Expression;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.*;
import com.querydsl.core.types.dsl.StringTemplate;
import com.querydsl.jpa.impl.JPAQueryFactory;
import kr.or.kmi.mis.api.apply.model.request.ApplyRequestDTO;
import kr.or.kmi.mis.api.bcd.model.entity.QBcdDetail;
import kr.or.kmi.mis.api.bcd.model.entity.QBcdMaster;
import kr.or.kmi.mis.api.bcd.model.response.BcdMasterResponseDTO;
import kr.or.kmi.mis.api.bcd.repository.BcdApplyQueryRepository;
import kr.or.kmi.mis.api.bcd.repository.BcdSampleQueryRepository;
import kr.or.kmi.mis.api.std.service.StdBcdService;
import kr.or.kmi.mis.cmm.model.request.PostSearchRequestDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

/**
 * packageName    : kr.or.kmi.mis.api.bcd.repository.impl
 * fileName       : BcdApplyQueryRepositoryImpl
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
public class BcdApplyQueryRepositoryImpl implements BcdApplyQueryRepository {

    private final JPAQueryFactory queryFactory;
    private final QBcdMaster bcdMaster = QBcdMaster.bcdMaster;
    private final QBcdDetail bcdDetail = QBcdDetail.bcdDetail;
    private final StdBcdService stdBcdService;

    @Override
    public Page<BcdMasterResponseDTO> getBcdApply2(ApplyRequestDTO applyRequestDTO, PostSearchRequestDTO postSearchRequestDTO, Pageable page) {

        String instNm = stdBcdService.getInstNm(applyRequestDTO.getInstCd());
        String docType = "명함신청";

        List<BcdMasterResponseDTO> resultSet = queryFactory.select(
                    Projections.constructor(
                            BcdMasterResponseDTO.class,
                            bcdMaster.draftId,
                            bcdMaster.title,
                            bcdDetail.instCd,
                            Expressions.constant(instNm),
                            bcdMaster.draftDate,
                            bcdMaster.respondDate,
                            bcdMaster.orderDate,
                            bcdMaster.drafter,
                            bcdMaster.status,
                            bcdDetail.lastUpdtr,
                            bcdDetail.lastupdtDate,
                            Expressions.constant(docType)
                    )
                )
                .from(bcdMaster)
                .leftJoin(bcdDetail).on(bcdMaster.draftId.eq(bcdDetail.draftId))
                .where(

                )
                .orderBy(bcdMaster.rgstDt.desc())
                .offset(page.getOffset())
                .limit(page.getPageSize())
                .fetch();

        Long count = queryFactory.select(bcdMaster.count())
                .from(bcdMaster)
                .leftJoin(bcdDetail).on(bcdMaster.draftId.eq(bcdDetail.draftId))
                .where(
                        titleContains(postSearchRequestDTO.getKeyword()),
                        afterStartDate(StringUtils.hasLength(postSearchRequestDTO.getStartDate()) ?
                                LocalDate.parse(postSearchRequestDTO.getStartDate()) : null),    // 검색 - 등록일자(시작)
                        beforeEndDate(StringUtils.hasLength(postSearchRequestDTO.getEndDate()) ?
                                LocalDate.parse(postSearchRequestDTO.getEndDate()) : null)   // 검색 - 등록일자(끝)
                )
                .fetchOne();

        return new PageImpl<>(resultSet, page, count);
    }

    private BooleanExpression titleContains(String title) {
        return StringUtils.hasLength(title) ? bcdMaster.title.like("%" + title + "%") : null;
    }

    private BooleanExpression afterStartDate(LocalDate startDate) {
//        return startDate != null ? bcdMaster.rgstDt.after(startDate.atStartOfDay()).or(bcdMaster.rgstDt.eq(startDate.atStartOfDay())) : null;
        return null;
    }

    private BooleanExpression beforeEndDate(LocalDate endDate) {
//        return endDate != null ? bcdMaster.rgstDt.before(endDate.atTime(LocalTime.MAX)).or(bcdMaster.rgstDt.eq(endDate.atTime(LocalTime.MAX))) : null;
        return null;
    }
}
