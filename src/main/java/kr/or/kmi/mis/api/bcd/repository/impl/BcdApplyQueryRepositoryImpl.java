package kr.or.kmi.mis.api.bcd.repository.impl;

import com.querydsl.core.types.Expression;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.*;
import com.querydsl.core.types.dsl.StringTemplate;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import kr.or.kmi.mis.api.apply.model.request.ApplyRequestDTO;
import kr.or.kmi.mis.api.bcd.model.entity.BcdDetail;
import kr.or.kmi.mis.api.bcd.model.entity.BcdMaster;
import kr.or.kmi.mis.api.bcd.model.entity.QBcdDetail;
import kr.or.kmi.mis.api.bcd.model.entity.QBcdMaster;
import kr.or.kmi.mis.api.bcd.model.response.BcdMasterResponseDTO;
import kr.or.kmi.mis.api.bcd.model.response.BcdMyResponseDTO;
import kr.or.kmi.mis.api.bcd.repository.BcdApplyQueryRepository;
import kr.or.kmi.mis.api.bcd.repository.BcdSampleQueryRepository;
import kr.or.kmi.mis.api.std.service.StdBcdService;
import kr.or.kmi.mis.api.user.service.InfoService;
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
    private final InfoService infoService;

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
                            Expressions.constant(docType),
                            bcdMaster.approverChain,
                            bcdMaster.currentApproverIndex
                            )
                )
                .from(bcdMaster)
                .leftJoin(bcdDetail).on(bcdMaster.draftId.eq(bcdDetail.draftId))
                .where(
                        bcdMaster.status.ne("F"),
                        bcdDetail.instCd.eq(applyRequestDTO.getInstCd()),
                        this.titleContains(postSearchRequestDTO.getSearchType(), postSearchRequestDTO.getKeyword()),
                        this.afterStartDate(StringUtils.hasLength(postSearchRequestDTO.getStartDate()) ?
                                LocalDate.parse(postSearchRequestDTO.getStartDate()) : null),    // 검색 - 등록일자(시작)
                        this.beforeEndDate(StringUtils.hasLength(postSearchRequestDTO.getEndDate()) ?
                                LocalDate.parse(postSearchRequestDTO.getEndDate()) : null)   // 검색 - 등록일자(끝)
                )
                .orderBy(bcdMaster.rgstDt.desc())
                .offset(page.getOffset())
                .limit(page.getPageSize())
                .fetch();

        Long count = queryFactory.select(bcdMaster.count())
                .from(bcdMaster)
                .leftJoin(bcdDetail).on(bcdMaster.draftId.eq(bcdDetail.draftId))
                .where(
                        bcdMaster.status.ne("F"),
                        bcdDetail.instCd.eq(applyRequestDTO.getInstCd()),
                        this.titleContains(postSearchRequestDTO.getSearchType(), postSearchRequestDTO.getKeyword()),
                        this.afterStartDate(StringUtils.hasLength(postSearchRequestDTO.getStartDate()) ?
                                LocalDate.parse(postSearchRequestDTO.getStartDate()) : null),    // 검색 - 등록일자(시작)
                        this.beforeEndDate(StringUtils.hasLength(postSearchRequestDTO.getEndDate()) ?
                                LocalDate.parse(postSearchRequestDTO.getEndDate()) : null)   // 검색 - 등록일자(끝)
                )
                .fetchOne();

        return new PageImpl<>(resultSet, page, count);
    }

    @Override
    public Page<BcdMyResponseDTO> getMyBcdApply2(ApplyRequestDTO applyRequestDTO, PostSearchRequestDTO postSearchRequestDTO, Pageable page) {

        List<BcdMaster> bcdMasters = queryFactory.select(bcdMaster)
                .from(bcdMaster)
                .where(
                        bcdMaster.drafterId.eq(applyRequestDTO.getUserId()),
                        this.afterStartDate(StringUtils.hasLength(postSearchRequestDTO.getStartDate()) ?
                                LocalDate.parse(postSearchRequestDTO.getStartDate()) : null),    // 검색 - 등록일자(시작)
                        this.beforeEndDate(StringUtils.hasLength(postSearchRequestDTO.getEndDate()) ?
                                LocalDate.parse(postSearchRequestDTO.getEndDate()) : null)   // 검색 - 등록일자(끝)
                )
                .orderBy(bcdMaster.rgstDt.desc())
                .offset(page.getOffset())
                .limit(page.getPageSize())
                .fetch();

        List<BcdMyResponseDTO> resultSet = bcdMasters.stream()
                .map(bcdMaster -> BcdMyResponseDTO.of(bcdMaster, infoService))
                .collect(Collectors.toList());

        Long count = queryFactory.select(bcdMaster.count())
                .from(bcdMaster)
                .where(
                        bcdMaster.drafterId.eq(applyRequestDTO.getUserId()),
                        this.afterStartDate(StringUtils.hasLength(postSearchRequestDTO.getStartDate()) ?
                                LocalDate.parse(postSearchRequestDTO.getStartDate()) : null),    // 검색 - 등록일자(시작)
                        this.beforeEndDate(StringUtils.hasLength(postSearchRequestDTO.getEndDate()) ?
                                LocalDate.parse(postSearchRequestDTO.getEndDate()) : null)   // 검색 - 등록일자(끝)
                )
                .orderBy(bcdMaster.rgstDt.desc())
                .fetchOne();

        return new PageImpl<>(resultSet, page, count);
    }

    @Override
    public List<BcdMyResponseDTO> getMyBcdList(ApplyRequestDTO applyRequestDTO, PostSearchRequestDTO postSearchRequestDTO) {

        List<BcdMaster> bcdMasters = queryFactory.select(bcdMaster)
                .from(bcdMaster)
                .leftJoin(bcdDetail).on(bcdMaster.draftId.eq(bcdDetail.draftId))
                .where(
                        bcdMaster.drafterId.eq(applyRequestDTO.getUserId()),
                        this.afterStartDate(StringUtils.hasLength(postSearchRequestDTO.getStartDate()) ?
                                LocalDate.parse(postSearchRequestDTO.getStartDate()) : null),    // 검색 - 등록일자(시작)
                        this.beforeEndDate(StringUtils.hasLength(postSearchRequestDTO.getEndDate()) ?
                                LocalDate.parse(postSearchRequestDTO.getEndDate()) : null)   // 검색 - 등록일자(끝)
                )
                .orderBy(bcdMaster.rgstDt.desc())
                .fetch();

        List<BcdMyResponseDTO> resultSet;
        resultSet = bcdMasters.stream()
                .map(bcdMaster -> BcdMyResponseDTO.of(bcdMaster, infoService))
                .collect(Collectors.toList());

        return resultSet;
    }

    @Override
    public List<BcdMyResponseDTO> getAnotherMasterList(ApplyRequestDTO applyRequestDTO, PostSearchRequestDTO postSearchRequestDTO) {

        List<BcdMaster> bcdMasters = queryFactory.select(bcdMaster)
                .from(bcdMaster)
                .leftJoin(bcdDetail).on(bcdMaster.draftId.eq(bcdDetail.draftId))
                .where(
                        bcdDetail.userId.eq(applyRequestDTO.getUserId()),
                        bcdMaster.drafterId.ne(applyRequestDTO.getUserId()),
                        this.afterStartDate(StringUtils.hasLength(postSearchRequestDTO.getStartDate()) ?
                                LocalDate.parse(postSearchRequestDTO.getStartDate()) : null),    // 검색 - 등록일자(시작)
                        this.beforeEndDate(StringUtils.hasLength(postSearchRequestDTO.getEndDate()) ?
                                LocalDate.parse(postSearchRequestDTO.getEndDate()) : null)   // 검색 - 등록일자(끝)
                )
                .orderBy(bcdMaster.rgstDt.desc())
                .fetch();

        List<BcdMyResponseDTO> resultSet = bcdMasters.stream()
                .map(bcdMaster -> BcdMyResponseDTO.of(bcdMaster, infoService)) // infoService를 활용하여 BcdMyResponseDTO 변환
                .collect(Collectors.toList());

        return resultSet;

    }

    private BooleanExpression titleContains(String searchType, String title) {
        if (StringUtils.hasLength(searchType) && StringUtils.hasLength(title)) {
            switch (searchType) {
                case "전체": return bcdMaster.title.containsIgnoreCase(title).or(bcdMaster.drafter.containsIgnoreCase(title));
                case "제목": return bcdMaster.title.containsIgnoreCase(title);
                case "신청자": return bcdMaster.drafter.containsIgnoreCase(title);
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
