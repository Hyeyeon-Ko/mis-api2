package kr.or.kmi.mis.api.seal.repository.impl;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import kr.or.kmi.mis.api.apply.model.request.ApplyRequestDTO;
import kr.or.kmi.mis.api.seal.model.entity.QSealMaster;
import kr.or.kmi.mis.api.seal.model.response.SealPendingResponseDTO;
import kr.or.kmi.mis.api.seal.repository.SealExportDetailRepository;
import kr.or.kmi.mis.api.seal.repository.SealPendingQueryRepository;
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
public class SealPendingQueryRepositoryImpl implements SealPendingQueryRepository {

    private final JPAQueryFactory queryFactory;
    private final QSealMaster sealMaster = QSealMaster.sealMaster;
    private final StdBcdService stdBcdService;
    private final SealExportDetailRepository sealExportDetailRepository;

    @Override
    public Page<SealPendingResponseDTO> getSealPending2(ApplyRequestDTO applyRequestDTO, PostSearchRequestDTO postSearchRequestDTO, Pageable page) {

        String instNm = stdBcdService.getInstNm(applyRequestDTO.getInstCd());

        List<SealPendingResponseDTO> resultSet = queryFactory.select(
                        Projections.constructor(
                                SealPendingResponseDTO.class,
                                sealMaster.draftId,
                                sealMaster.title,
                                sealMaster.instCd,
                                Expressions.constant(instNm),
                                sealMaster.draftDate,
                                sealMaster.drafter,
                                sealMaster.updtDt,
                                sealMaster.drafter,
                                sealMaster.status,
                                Expressions.stringTemplate("case when {0} = 'A' then '인장신청(날인)' else '인장신청(반출)' end", sealMaster.division).as("docType")
                        )
                )
                .from(sealMaster)
                .where(
                        sealMaster.status.eq("A"),
                        sealMaster.instCd.eq(applyRequestDTO.getInstCd()),
                        this.afterStartDate(StringUtils.hasLength(postSearchRequestDTO.getStartDate()) ?
                                LocalDate.parse(postSearchRequestDTO.getStartDate()) : null),    // 검색 - 등록일자(시작)
                        this.beforeEndDate(StringUtils.hasLength(postSearchRequestDTO.getEndDate()) ?
                                LocalDate.parse(postSearchRequestDTO.getEndDate()) : null)   // 검색 - 등록일자(끝)
                )
                .orderBy(sealMaster.rgstDt.desc())
                .offset(page.getOffset())
                .limit(page.getPageSize())
                .fetch();

        Long count = queryFactory.select(sealMaster.count())
                .from(sealMaster)
                .where(
                        sealMaster.status.eq("A"),
                        sealMaster.instCd.eq(applyRequestDTO.getInstCd()),
                        this.afterStartDate(StringUtils.hasLength(postSearchRequestDTO.getStartDate()) ?
                                LocalDate.parse(postSearchRequestDTO.getStartDate()) : null),    // 검색 - 등록일자(시작)
                        this.beforeEndDate(StringUtils.hasLength(postSearchRequestDTO.getEndDate()) ?
                                LocalDate.parse(postSearchRequestDTO.getEndDate()) : null)   // 검색 - 등록일자(끝)
                )
                .fetchOne();

        return new PageImpl<>(resultSet, page, count);
    }

    private BooleanExpression afterStartDate(LocalDate startDate) {
        if (startDate == null) {
            return Expressions.asBoolean(true).isTrue();
        }
        LocalDateTime startDateTime = startDate.atStartOfDay();
        return sealMaster.draftDate.goe(startDateTime);
    }

    private BooleanExpression beforeEndDate(LocalDate endDate) {
        if (endDate == null) {
            return Expressions.asBoolean(true).isTrue();
        }
        LocalDateTime endDateTime = endDate.atTime(LocalTime.MAX);
        return sealMaster.draftDate.loe(endDateTime);
    }

}
