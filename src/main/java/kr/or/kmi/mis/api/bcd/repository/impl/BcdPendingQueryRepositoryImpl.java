package kr.or.kmi.mis.api.bcd.repository.impl;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import kr.or.kmi.mis.api.apply.model.request.ApplyRequestDTO;
import kr.or.kmi.mis.api.bcd.model.entity.QBcdDetail;
import kr.or.kmi.mis.api.bcd.model.entity.QBcdMaster;
import kr.or.kmi.mis.api.bcd.model.response.BcdPendingResponseDTO;
import kr.or.kmi.mis.api.bcd.repository.BcdPendingQueryRepository;
import kr.or.kmi.mis.api.std.service.StdBcdService;
import kr.or.kmi.mis.cmm.model.request.PostSearchRequestDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class BcdPendingQueryRepositoryImpl implements BcdPendingQueryRepository {

    private final JPAQueryFactory queryFactory;
    private final QBcdMaster bcdMaster = QBcdMaster.bcdMaster;
    private final QBcdDetail bcdDetail = QBcdDetail.bcdDetail;
    private final StdBcdService stdBcdService;

    @Override
    public Page<BcdPendingResponseDTO> getBcdPending2(ApplyRequestDTO applyRequestDTO, PostSearchRequestDTO postSearchRequestDTO, Pageable page) {

        String instNm = stdBcdService.getInstNm(applyRequestDTO.getInstCd());

        String docType = "명함신청";

        List<BcdPendingResponseDTO> resultSet = queryFactory.select(
                        Projections.constructor(
                                BcdPendingResponseDTO.class,
                                bcdMaster.draftId,
                                bcdMaster.title,
                                bcdDetail.instCd,
                                Expressions.constant(instNm),
                                bcdMaster.draftDate,
                                bcdMaster.drafter,
                                bcdDetail.lastupdtDate,
                                bcdDetail.lastUpdtr,
                                bcdMaster.status,
                                Expressions.constant(docType)
                        )
                )
                .from(bcdMaster)
                .leftJoin(bcdDetail).on(bcdMaster.draftId.eq(bcdDetail.draftId))
                .where(
                        bcdMaster.status.eq("A"),
                        bcdDetail.instCd.eq(applyRequestDTO.getInstCd()),
                        this.afterStartDate(StringUtils.hasLength(postSearchRequestDTO.getStartDate()) ?
                                LocalDate.parse(postSearchRequestDTO.getStartDate()) : null),    // 검색 - 등록일자(시작)
                        this.beforeEndDate(StringUtils.hasLength(postSearchRequestDTO.getEndDate()) ?
                                LocalDate.parse(postSearchRequestDTO.getEndDate()) : null)   // 검색 - 등록일자(끝)
                )
                .orderBy(bcdMaster.rgstDt.asc())
                .offset(page.getOffset())
                .limit(page.getPageSize())
                .fetch();

        Long count = queryFactory.select(bcdMaster.count())
                .from(bcdMaster)
                .leftJoin(bcdDetail).on(bcdMaster.draftId.eq(bcdDetail.draftId))
                .where(
                        bcdMaster.status.eq("A"),
                        bcdDetail.instCd.eq(applyRequestDTO.getInstCd()),
                        this.afterStartDate(StringUtils.hasLength(postSearchRequestDTO.getStartDate()) ?
                                LocalDate.parse(postSearchRequestDTO.getStartDate()) : null),    // 검색 - 등록일자(시작)
                        this.beforeEndDate(StringUtils.hasLength(postSearchRequestDTO.getEndDate()) ?
                                LocalDate.parse(postSearchRequestDTO.getEndDate()) : null)   // 검색 - 등록일자(끝)
                )
                .fetchOne();

        return new PageImpl<>(resultSet, page, count);
    }

    @Override
    public Page<BcdPendingResponseDTO> getMyBcdPendingList2(ApplyRequestDTO applyRequestDTO, Pageable page) {

        String instNm = stdBcdService.getInstNm(applyRequestDTO.getInstCd());

        String docType = "명함신청";

        List<BcdPendingResponseDTO> resultSet = queryFactory.select(
                Projections.constructor(
                        BcdPendingResponseDTO.class,
                        bcdMaster.draftId,
                        bcdMaster.title,
                        bcdDetail.instCd,
                        Expressions.constant(instNm),
                        bcdMaster.draftDate,
                        bcdMaster.drafter,
                        bcdDetail.lastupdtDate,
                        bcdDetail.lastUpdtr,
                        bcdMaster.status,
                        Expressions.constant(docType)
                        )
                )
                .from(bcdMaster)
                .join(bcdDetail).on(bcdMaster.draftId.eq(bcdDetail.draftId))
                .where(
                        bcdDetail.userId.eq(applyRequestDTO.getUserId())
                                .and(bcdMaster.status.eq("A"))
                                .and(bcdMaster.drafterId.ne(applyRequestDTO.getUserId()))
                )
                .orderBy(bcdMaster.rgstDt.desc())
                .offset(page.getOffset())
                .limit(page.getPageSize())
                .fetch();

        Long count = queryFactory
                .select(bcdMaster.count())
                .from(bcdMaster)
                .join(bcdDetail).on(bcdMaster.draftId.eq(bcdDetail.draftId))
                .where(
                        bcdDetail.userId.eq(applyRequestDTO.getUserId())
                                .and(bcdMaster.status.eq("A"))
                                .and(bcdMaster.drafterId.ne(applyRequestDTO.getUserId()))
                )
                .fetchOne();

        return new PageImpl<>(resultSet, page, count);
    }

    @Override
    public Long getBcdPendingCount(ApplyRequestDTO applyRequestDTO, PostSearchRequestDTO postSearchRequestDTO) {
        return queryFactory.select(bcdMaster.count())
                .from(bcdMaster)
                .leftJoin(bcdDetail).on(bcdMaster.draftId.eq(bcdDetail.draftId))
                .where(
                        bcdMaster.status.eq("A"),
                        bcdDetail.instCd.eq(applyRequestDTO.getInstCd()),
                        this.afterStartDate(StringUtils.hasLength(postSearchRequestDTO.getStartDate()) ?
                                LocalDate.parse(postSearchRequestDTO.getStartDate()) : null),
                        this.beforeEndDate(StringUtils.hasLength(postSearchRequestDTO.getEndDate()) ?
                                LocalDate.parse(postSearchRequestDTO.getEndDate()) : null)
                )
                .fetchOne();
    }

    private BooleanExpression afterStartDate(LocalDate startDate) {
        if (startDate == null) {
            return Expressions.asBoolean(true).isTrue();
        }
        LocalDateTime startDateTime = startDate.atStartOfDay();
        return bcdMaster.draftDate.goe(startDateTime);
    }

    private BooleanExpression beforeEndDate(LocalDate endDate) {
        if (endDate == null) {
            return Expressions.asBoolean(true).isTrue();
        }
        LocalDateTime endDateTime = endDate.atTime(LocalTime.MAX);
        return bcdMaster.draftDate.loe(endDateTime);
    }

}