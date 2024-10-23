package kr.or.kmi.mis.api.toner.repository.impl;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import kr.or.kmi.mis.api.apply.model.request.ApplyRequestDTO;
import kr.or.kmi.mis.api.seal.model.entity.SealMaster;
import kr.or.kmi.mis.api.seal.model.response.SealMyResponseDTO;
import kr.or.kmi.mis.api.toner.model.entity.QTonerMaster;
import kr.or.kmi.mis.api.toner.model.entity.TonerInfo;
import kr.or.kmi.mis.api.toner.model.entity.TonerMaster;
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
        String docType = "토너신청";
        List<TonerMyResponseDTO> resultSet = queryFactory.select(
                Projections.constructor(
                        TonerMyResponseDTO.class,
                        tonerMaster.draftId,
                        tonerMaster.title,
                        tonerMaster.draftDate,
                        tonerMaster.respondDate,
                        tonerMaster.drafter,
                        tonerMaster.approver,
                        tonerMaster.disapprover,
                        tonerMaster.status,
                        tonerMaster.rejectReason,
                        Expressions.constant(docType)
                )
            )
            .from(tonerMaster)
                .where(
                        tonerMaster.draftId.eq(applyRequestDTO.getUserId()),
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
}
