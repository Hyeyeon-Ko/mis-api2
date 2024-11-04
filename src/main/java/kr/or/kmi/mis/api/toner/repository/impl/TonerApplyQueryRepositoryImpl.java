package kr.or.kmi.mis.api.toner.repository.impl;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import kr.or.kmi.mis.api.apply.model.request.ApplyRequestDTO;
import kr.or.kmi.mis.api.std.service.StdBcdService;
import kr.or.kmi.mis.api.toner.model.entity.QTonerMaster;
import kr.or.kmi.mis.api.toner.model.entity.TonerMaster;
import kr.or.kmi.mis.api.toner.model.response.TonerMasterResponseDTO;
import kr.or.kmi.mis.api.toner.model.response.TonerMyResponseDTO;
import kr.or.kmi.mis.api.toner.repository.TonerApplyQueryRepository;
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
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class TonerApplyQueryRepositoryImpl implements TonerApplyQueryRepository {

    private final JPAQueryFactory queryFactory;
    private final QTonerMaster tonerMaster = QTonerMaster.tonerMaster;
    private final StdBcdService stdBcdService;

    @Override
    public List<TonerMyResponseDTO> getMyTonerApply(ApplyRequestDTO applyRequestDTO, PostSearchRequestDTO postSearchRequestDTO) {
        List<TonerMaster> tonerMasters = queryFactory.select(tonerMaster)
                .from(tonerMaster)
                .where(
                        tonerMaster.drafterId.eq(applyRequestDTO.getUserId()),
                        this.afterStartDate(StringUtils.hasLength(postSearchRequestDTO.getStartDate()) ?
                                LocalDate.parse(postSearchRequestDTO.getStartDate()) : null),    // 검색 - 등록일자(시작)
                        this.beforeEndDate(StringUtils.hasLength(postSearchRequestDTO.getEndDate()) ?
                                LocalDate.parse(postSearchRequestDTO.getEndDate()) : null),   // 검색 - 등록일자(끝)
                        applyStatusIn(postSearchRequestDTO.getApplyStatus())
                )
                .fetch();

        return tonerMasters.stream()
                .map(TonerMyResponseDTO::of)
                .collect(Collectors.toList());
    }

    @Override
    public Page<TonerMyResponseDTO> getMyTonerApply2(ApplyRequestDTO applyRequestDTO, PostSearchRequestDTO postSearchRequestDTO, Pageable page) {
        List<TonerMaster> tonerMasters = queryFactory.select(tonerMaster)
                .from(tonerMaster)
                .where(
                        tonerMaster.drafterId.eq(applyRequestDTO.getUserId()),
                        this.afterStartDate(StringUtils.hasLength(postSearchRequestDTO.getStartDate()) ?
                                LocalDate.parse(postSearchRequestDTO.getStartDate()) : null),    // 검색 - 등록일자(시작)
                        this.beforeEndDate(StringUtils.hasLength(postSearchRequestDTO.getEndDate()) ?
                                LocalDate.parse(postSearchRequestDTO.getEndDate()) : null),   // 검색 - 등록일자(끝)
                        applyStatusIn(postSearchRequestDTO.getApplyStatus())
                )
                .orderBy(tonerMaster.rgstDt.desc())
                .offset(page.getOffset())
                .limit(page.getPageSize())
                .fetch();

        List<TonerMyResponseDTO> resultSet = tonerMasters.stream()
                .map(TonerMyResponseDTO::of)
                .collect(Collectors.toList());

        Long count = queryFactory.select(tonerMaster.count())
                .from(tonerMaster)
                .where(
                        tonerMaster.drafterId.eq(applyRequestDTO.getUserId()),
                        this.afterStartDate(StringUtils.hasLength(postSearchRequestDTO.getStartDate()) ?
                                LocalDate.parse(postSearchRequestDTO.getStartDate()) : null),    // 검색 - 등록일자(시작)
                        this.beforeEndDate(StringUtils.hasLength(postSearchRequestDTO.getEndDate()) ?
                                LocalDate.parse(postSearchRequestDTO.getEndDate()) : null),   // 검색 - 등록일자(끝)
                        applyStatusIn(postSearchRequestDTO.getApplyStatus())
                )
                .fetchOne();

        return new PageImpl<>(resultSet, page, count);
    }

    @Override
    public Page<TonerMasterResponseDTO> getTonerApply2(ApplyRequestDTO applyRequestDTO, PostSearchRequestDTO postSearchRequestDTO, Pageable pageable) {

        String instNm = stdBcdService.getInstNm(applyRequestDTO.getInstCd());
        String docType = "토너신청";

        List<TonerMasterResponseDTO> resultSet = queryFactory.select(
                        Projections.constructor(
                                TonerMasterResponseDTO.class,
                                tonerMaster.draftId,
                                tonerMaster.title,
                                tonerMaster.instCd,
                                Expressions.constant(instNm),
                                tonerMaster.draftDate,
                                tonerMaster.respondDate,
                                tonerMaster.orderDate,
                                tonerMaster.drafter,
                                tonerMaster.status,
                                Expressions.constant(docType)
                        )
                )
                .from(tonerMaster)
                .where(
                        tonerMaster.instCd.eq(applyRequestDTO.getInstCd()),
                        this.titleContains(postSearchRequestDTO.getSearchType(), postSearchRequestDTO.getKeyword()),
                        this.afterStartDate(StringUtils.hasLength(postSearchRequestDTO.getStartDate()) ?
                                LocalDate.parse(postSearchRequestDTO.getStartDate()) : null),    // 검색 - 등록일자(시작)
                        this.beforeEndDate(StringUtils.hasLength(postSearchRequestDTO.getEndDate()) ?
                                LocalDate.parse(postSearchRequestDTO.getEndDate()) : null)   // 검색 - 등록일자(끝)
                )
                .orderBy(tonerMaster.draftDate.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long count = queryFactory.select(tonerMaster.count())
                .from(tonerMaster)
                .where(
                        tonerMaster.instCd.eq(applyRequestDTO.getInstCd()),
                        this.titleContains(postSearchRequestDTO.getSearchType(), postSearchRequestDTO.getKeyword()),
                        this.afterStartDate(StringUtils.hasLength(postSearchRequestDTO.getStartDate()) ?
                                LocalDate.parse(postSearchRequestDTO.getStartDate()) : null),    // 검색 - 등록일자(시작)
                        this.beforeEndDate(StringUtils.hasLength(postSearchRequestDTO.getEndDate()) ?
                                LocalDate.parse(postSearchRequestDTO.getEndDate()) : null)   // 검색 - 등록일자(끝)
                )
                .fetchOne();

        return new PageImpl<>(resultSet, pageable, count);
    }

    private BooleanExpression applyStatusIn(List<String> applyStatus) {
        if (applyStatus == null || applyStatus.isEmpty()) {
            return null; // 조건이 없을 경우 필터링 하지 않음
        }
        return tonerMaster.status.in(applyStatus); // applyStatus 리스트에 해당하는 값만 필터링
    }

    private BooleanExpression afterStartDate(LocalDate startDate) {
        if (startDate == null) {
            return Expressions.asBoolean(true).isTrue();
        }
        LocalDateTime startDateTime = startDate.atStartOfDay();
        return tonerMaster.draftDate.goe(startDateTime);
    }

    private BooleanExpression beforeEndDate(LocalDate endDate) {
        if (endDate == null) {
            return Expressions.asBoolean(true).isTrue();
        }
        LocalDateTime endDateTime = endDate.atTime(LocalTime.MAX);
        return tonerMaster.draftDate.loe(endDateTime);
    }

    private BooleanExpression titleContains(String searchType, String title) {
        if (StringUtils.hasLength(searchType) && StringUtils.hasLength(title)) {
            switch (searchType) {
                case "전체": return tonerMaster.title.containsIgnoreCase(title).or(tonerMaster.drafter.containsIgnoreCase(title));
                case "제목": return tonerMaster.title.containsIgnoreCase(title);
                case "신청자": return tonerMaster.drafter.containsIgnoreCase(title);
                default: return null;
            }
        }
        return null;
    }
}
