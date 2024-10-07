package kr.or.kmi.mis.api.corpdoc.repository.impl;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import kr.or.kmi.mis.api.apply.model.request.ApplyRequestDTO;
import kr.or.kmi.mis.api.corpdoc.model.entity.QCorpDocDetail;
import kr.or.kmi.mis.api.corpdoc.model.entity.QCorpDocMaster;
import kr.or.kmi.mis.api.corpdoc.model.response.CorpDocPendingResponseDTO;
import kr.or.kmi.mis.api.corpdoc.repository.CorpDocPendingQueryRepository;
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
public class CorpDocPendingQueryRepositoryImpl implements CorpDocPendingQueryRepository {

    private final JPAQueryFactory queryFactory;
    private final QCorpDocMaster corpDocMaster = QCorpDocMaster.corpDocMaster;
    private final QCorpDocDetail corpDocDetail = QCorpDocDetail.corpDocDetail;
    private final StdBcdService stdBcdService;

    @Override
    public Page<CorpDocPendingResponseDTO> getCorpDocPending2(ApplyRequestDTO applyRequestDTO, PostSearchRequestDTO postSearchRequestDTO, Pageable page) {

        String instNm = stdBcdService.getInstNm(applyRequestDTO.getInstCd());

        String docType = "법인서류";

        List<CorpDocPendingResponseDTO> resultSet = queryFactory.select(
                        Projections.constructor(
                                CorpDocPendingResponseDTO.class,
                                corpDocMaster.draftId,
                                corpDocMaster.title,
                                corpDocMaster.instCd,
                                Expressions.constant(instNm),
                                corpDocMaster.draftDate,
                                corpDocMaster.drafter,
                                corpDocDetail.updtDt,
                                corpDocDetail.updtrId,
                                corpDocMaster.status,
                                Expressions.constant(docType)
                        )
                )
                .from(corpDocMaster)
                .leftJoin(corpDocDetail).on(corpDocMaster.draftId.eq(corpDocDetail.draftId))
                .where(
                        corpDocMaster.status.eq("A"),
                        this.afterStartDate(StringUtils.hasLength(postSearchRequestDTO.getStartDate()) ?
                                LocalDate.parse(postSearchRequestDTO.getStartDate()) : null),    // 검색 - 등록일자(시작)
                        this.beforeEndDate(StringUtils.hasLength(postSearchRequestDTO.getEndDate()) ?
                                LocalDate.parse(postSearchRequestDTO.getEndDate()) : null)   // 검색 - 등록일자(끝)
                )
                .orderBy(corpDocMaster.rgstDt.desc())
                .offset(page.getOffset())
                .limit(page.getPageSize())
                .fetch();

        Long count = queryFactory.select(corpDocMaster.count())
                .from(corpDocMaster)
                .where(
                        corpDocMaster.status.eq("A"),
                        this.afterStartDate(StringUtils.hasLength(postSearchRequestDTO.getStartDate()) ?
                                LocalDate.parse(postSearchRequestDTO.getStartDate()) : null),    // 검색 - 등록일자(시작)
                        this.beforeEndDate(StringUtils.hasLength(postSearchRequestDTO.getEndDate()) ?
                                LocalDate.parse(postSearchRequestDTO.getEndDate()) : null)   // 검색 - 등록일자(끝)
                )
                .fetchOne();

        return new PageImpl<>(resultSet, page, count);
    }

    @Override
    public Page<CorpDocPendingResponseDTO> getMyCorpDocPendingList2(ApplyRequestDTO applyRequestDTO, Pageable page) {
        System.out.println("CorpDocPendingQueryRepositoryImpl.getMyCorpDocPendingList2");

        String instNm = stdBcdService.getInstNm(applyRequestDTO.getInstCd());

        String docType = "법인서류";

        List<CorpDocPendingResponseDTO> resultSet = queryFactory.select(
                        Projections.constructor(
                                CorpDocPendingResponseDTO.class,
                                corpDocMaster.draftId,
                                corpDocMaster.title,
                                corpDocMaster.instCd,
                                Expressions.constant(instNm),
                                corpDocMaster.draftDate,
                                corpDocMaster.drafter,
                                corpDocDetail.updtDt,
                                corpDocDetail.updtrId,
                                corpDocMaster.status,
                                Expressions.constant(docType)
                        )
                )
                .from(corpDocMaster)
                .join(corpDocDetail).on(corpDocMaster.draftId.eq(corpDocDetail.draftId))
                .where(
                        corpDocMaster.drafterId.eq(applyRequestDTO.getUserId()),
                        corpDocMaster.status.eq("A")
                )
                .orderBy(corpDocMaster.rgstDt.desc())
                .offset(page.getOffset())
                .limit(page.getPageSize())
                .fetch();

        Long count = queryFactory
                .select(corpDocMaster.count())
                .from(corpDocMaster)
                .where(
                        corpDocMaster.drafterId.eq(applyRequestDTO.getUserId()),
                        corpDocMaster.status.eq("A")
                )
                .fetchOne();

        return new PageImpl<>(resultSet, page, count);
    }

    @Override
    public Long getCorpDocPendingCount(ApplyRequestDTO applyRequestDTO, PostSearchRequestDTO postSearchRequestDTO) {
        return queryFactory.select(corpDocMaster.count())
                .from(corpDocMaster)
                .where(
                        corpDocMaster.status.eq("A"),
                        this.afterStartDate(StringUtils.hasLength(postSearchRequestDTO.getStartDate()) ?
                                LocalDate.parse(postSearchRequestDTO.getStartDate()) : null),    // 검색 - 등록일자(시작)
                        this.beforeEndDate(StringUtils.hasLength(postSearchRequestDTO.getEndDate()) ?
                                LocalDate.parse(postSearchRequestDTO.getEndDate()) : null)   // 검색 - 등록일자(끝)
                )
                .fetchOne();
    }

    private BooleanExpression afterStartDate(LocalDate startDate) {
        if (startDate == null) {
            return Expressions.asBoolean(true).isTrue();
        }
        LocalDateTime startDateTime = startDate.atStartOfDay();
        return corpDocMaster.draftDate.goe(startDateTime);
    }

    private BooleanExpression beforeEndDate(LocalDate endDate) {
        if (endDate == null) {
            return Expressions.asBoolean(true).isTrue();
        }
        LocalDateTime endDateTime = endDate.atTime(LocalTime.MAX);
        return corpDocMaster.draftDate.loe(endDateTime);
    }

}
