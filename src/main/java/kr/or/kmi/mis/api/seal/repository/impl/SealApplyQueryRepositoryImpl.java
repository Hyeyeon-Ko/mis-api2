package kr.or.kmi.mis.api.seal.repository.impl;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import kr.or.kmi.mis.api.apply.model.request.ApplyRequestDTO;
import kr.or.kmi.mis.api.seal.model.entity.QSealMaster;
import kr.or.kmi.mis.api.seal.model.entity.SealMaster;
import kr.or.kmi.mis.api.seal.model.response.SealMasterResponseDTO;
import kr.or.kmi.mis.api.seal.model.response.SealMyResponseDTO;
import kr.or.kmi.mis.api.seal.repository.SealApplyQueryRepository;
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
import java.util.stream.Collectors;

/**
 * packageName    : kr.or.kmi.mis.api.seal.repository.impl
 * fileName       : SealApplyQueryRepositoryImpl
 * author         : KMI_DI
 * date           : 2024-10-04
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2024-10-04        KMI_DI       the first create
 */
@Repository
@RequiredArgsConstructor
public class SealApplyQueryRepositoryImpl implements SealApplyQueryRepository {

    private final JPAQueryFactory queryFactory;
    private final StdBcdService stdBcdService;
    private final QSealMaster sealMaster = QSealMaster.sealMaster;

    @Override
    public Page<SealMasterResponseDTO> getSealApply2(ApplyRequestDTO applyRequestDTO, PostSearchRequestDTO postSearchRequestDTO, Pageable page) {
        String instNm = stdBcdService.getInstNm(applyRequestDTO.getInstCd());

        List<SealMasterResponseDTO> resultSet = queryFactory.select(
                        Projections.constructor(
                                SealMasterResponseDTO.class,
                                sealMaster.draftId,
                                sealMaster.instCd,
                                Expressions.constant(instNm),
                                sealMaster.title,
                                sealMaster.draftDate,
                                sealMaster.respondDate,
                                sealMaster.drafter,
                                sealMaster.status,
                                Expressions.stringTemplate("case when {0} = 'A' then '인장신청(날인)' else '인장신청(반출)' end", sealMaster.division).as("docType")
                        )
                )
                .from(sealMaster)
                .where(
                        sealMaster.status.ne("F"),
                        sealMaster.instCd.eq(applyRequestDTO.getInstCd()),
                        this.titleContains(postSearchRequestDTO.getSearchType(), postSearchRequestDTO.getKeyword()),
                        this.afterStartDate(StringUtils.hasLength(postSearchRequestDTO.getStartDate()) ?
                                LocalDate.parse(postSearchRequestDTO.getStartDate()) : null),    // 검색 - 등록일자(시작)
                        this.beforeEndDate(StringUtils.hasLength(postSearchRequestDTO.getEndDate()) ?
                                LocalDate.parse(postSearchRequestDTO.getEndDate()) : null),   // 검색 - 등록일자(끝)
                        applyStatusIn(postSearchRequestDTO.getApplyStatus())      // 신청상태 필터링
                )
                .orderBy(sealMaster.rgstDt.desc())
                .offset(page.getOffset())
                .limit(page.getPageSize())
                .fetch();

        Long count = queryFactory.select(sealMaster.count())
                .from(sealMaster)
                .where(
                        sealMaster.status.ne("F"),
                        sealMaster.instCd.eq(applyRequestDTO.getInstCd()),
                        this.titleContains(postSearchRequestDTO.getSearchType(), postSearchRequestDTO.getKeyword()),
                        this.afterStartDate(StringUtils.hasLength(postSearchRequestDTO.getStartDate()) ?
                                LocalDate.parse(postSearchRequestDTO.getStartDate()) : null),    // 검색 - 등록일자(시작)
                        this.beforeEndDate(StringUtils.hasLength(postSearchRequestDTO.getEndDate()) ?
                                LocalDate.parse(postSearchRequestDTO.getEndDate()) : null),   // 검색 - 등록일자(끝)
                        applyStatusIn(postSearchRequestDTO.getApplyStatus())      // 신청상태 필터링
                )
                .fetchOne();

        return new PageImpl<>(resultSet, page, count);
    }

    private BooleanExpression applyStatusIn(List<String> applyStatus) {
        if (applyStatus == null || applyStatus.isEmpty()) {
            return null; // 조건이 없을 경우 필터링 하지 않음
        }
        return sealMaster.status.in(applyStatus); // applyStatus 리스트에 해당하는 값만 필터링
    }

    @Override
    public List<SealMyResponseDTO> getMySealApply(ApplyRequestDTO applyRequestDTO, PostSearchRequestDTO postSearchRequestDTO) {

        List<SealMaster> sealMasters = queryFactory.select(sealMaster)
                .from(sealMaster)
                .where(
                        sealMaster.drafterId.eq(applyRequestDTO.getUserId()),
                        this.afterStartDate(StringUtils.hasLength(postSearchRequestDTO.getStartDate()) ?
                                LocalDate.parse(postSearchRequestDTO.getStartDate()) : null),    // 검색 - 등록일자(시작)
                        this.beforeEndDate(StringUtils.hasLength(postSearchRequestDTO.getEndDate()) ?
                                LocalDate.parse(postSearchRequestDTO.getEndDate()) : null),   // 검색 - 등록일자(끝)
                        applyStatusIn(postSearchRequestDTO.getApplyStatus())
                )
                .fetch();

        return sealMasters.stream()
                .map(SealMyResponseDTO::of)
                .collect(Collectors.toList());
    }

    @Override
    public Page<SealMyResponseDTO> getMySealApply2(ApplyRequestDTO applyRequestDTO, PostSearchRequestDTO postSearchRequestDTO, Pageable page) {
        String docType = "인장신청";
        List<SealMyResponseDTO> resultSet = queryFactory.select(
                    Projections.constructor(
                            SealMyResponseDTO.class,
                            sealMaster.draftId,
                            sealMaster.title,
                            sealMaster.draftDate,
                            sealMaster.respondDate,
                            sealMaster.drafter,
                            sealMaster.approver,
                            sealMaster.disapprover,
                            sealMaster.status,
                            sealMaster.rejectReason,
                            Expressions.constant(docType)
                    )
                )
                .from(sealMaster)
                .where(
                        sealMaster.drafterId.eq(applyRequestDTO.getUserId()),
                        this.afterStartDate(StringUtils.hasLength(postSearchRequestDTO.getStartDate()) ?
                                LocalDate.parse(postSearchRequestDTO.getStartDate()) : null),    // 검색 - 등록일자(시작)
                        this.beforeEndDate(StringUtils.hasLength(postSearchRequestDTO.getEndDate()) ?
                                LocalDate.parse(postSearchRequestDTO.getEndDate()) : null),   // 검색 - 등록일자(끝)
                        applyStatusIn(postSearchRequestDTO.getApplyStatus())
                )
                .orderBy(sealMaster.rgstDt.desc())
                .offset(page.getOffset())
                .limit(page.getPageSize())
                .fetch();

        Long count = queryFactory.select(sealMaster.count())
                .from(sealMaster)
                .where(
                        sealMaster.drafterId.eq(applyRequestDTO.getUserId()),
                        this.afterStartDate(StringUtils.hasLength(postSearchRequestDTO.getStartDate()) ?
                                LocalDate.parse(postSearchRequestDTO.getStartDate()) : null),    // 검색 - 등록일자(시작)
                        this.beforeEndDate(StringUtils.hasLength(postSearchRequestDTO.getEndDate()) ?
                                LocalDate.parse(postSearchRequestDTO.getEndDate()) : null),   // 검색 - 등록일자(끝)
                        applyStatusIn(postSearchRequestDTO.getApplyStatus())
                )
                .fetchOne();

        return new PageImpl<>(resultSet, page, count);
    }

    private BooleanExpression titleContains(String searchType, String title) {
        if (StringUtils.hasLength(searchType) && StringUtils.hasLength(title)) {
            switch (searchType) {
                case "전체": return sealMaster.title.containsIgnoreCase(title).or(sealMaster.drafter.containsIgnoreCase(title));
                case "제목": return sealMaster.title.containsIgnoreCase(title);
                case "신청자": return sealMaster.drafter.containsIgnoreCase(title);
                default: return null;
            }
        }
        return null;
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
