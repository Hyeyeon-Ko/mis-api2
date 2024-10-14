package kr.or.kmi.mis.api.doc.repository.impl;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.core.types.dsl.StringPath;
import com.querydsl.jpa.impl.JPAQueryFactory;
import kr.or.kmi.mis.api.apply.model.request.ApplyRequestDTO;
import kr.or.kmi.mis.api.doc.model.entity.QDocDetail;
import kr.or.kmi.mis.api.doc.model.entity.QDocMaster;
import kr.or.kmi.mis.api.doc.model.response.DocPendingResponseDTO;
import kr.or.kmi.mis.api.doc.repository.DocDetailRepository;
import kr.or.kmi.mis.api.doc.repository.DocPendingQueryRepository;
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
public class DocPendingQueryRepositoryImpl implements DocPendingQueryRepository {

    private final JPAQueryFactory queryFactory;
    private final QDocMaster docMaster = QDocMaster.docMaster;
    private final QDocDetail docDetail = QDocDetail.docDetail;
    private final StdBcdService stdBcdService;
    private final DocDetailRepository docDetailRepository;

    @Override
    public Page<DocPendingResponseDTO> getDocPending2(ApplyRequestDTO applyRequestDTO, PostSearchRequestDTO postSearchRequestDTO, Pageable page) {

        String instNm = stdBcdService.getInstNm(applyRequestDTO.getInstCd());

        List<DocPendingResponseDTO> resultList = queryFactory.select(
                        Projections.constructor(
                                DocPendingResponseDTO.class,
                                docMaster.draftId,
                                docMaster.title,
                                docMaster.instCd,
                                Expressions.constant(instNm),
                                docMaster.draftDate,
                                docMaster.drafter,
                                docMaster.updtDt,
                                docMaster.drafter,
                                docMaster.status,
                                Expressions.stringTemplate("case when {0} = 'A' then '문서수신' else '문서발신' end", docDetail.division),
                                docMaster.approverChain,
                                docMaster.currentApproverIndex
                        )
                )
                .from(docMaster)
                .leftJoin(docDetail).on(docMaster.draftId.eq(docDetail.draftId))
                .where(
                        docMaster.status.eq("A"),
                        docMaster.instCd.eq(applyRequestDTO.getInstCd()),
                        this.afterStartDate(StringUtils.hasLength(postSearchRequestDTO.getStartDate()) ?
                                LocalDate.parse(postSearchRequestDTO.getStartDate()) : null),    // 검색 - 등록일자(시작)
                        this.beforeEndDate(StringUtils.hasLength(postSearchRequestDTO.getEndDate()) ?
                                LocalDate.parse(postSearchRequestDTO.getEndDate()) : null),   // 검색 - 등록일자(끝)
                        approverMatchCondition(applyRequestDTO.getUserId(), docMaster.approverChain, docMaster.currentApproverIndex)
                )
                .orderBy(docMaster.rgstDt.asc())
                .offset(page.getOffset())
                .limit(page.getPageSize())
                .fetch();

        Long count = queryFactory.select(docMaster.count())
                .from(docMaster)
                .leftJoin(docDetail).on(docMaster.draftId.eq(docDetail.draftId))
                .where(
                        docMaster.status.eq("A"),
                        docMaster.instCd.eq(applyRequestDTO.getInstCd()),
                        this.afterStartDate(StringUtils.hasLength(postSearchRequestDTO.getStartDate()) ?
                                LocalDate.parse(postSearchRequestDTO.getStartDate()) : null),    // 검색 - 등록일자(시작)
                        this.beforeEndDate(StringUtils.hasLength(postSearchRequestDTO.getEndDate()) ?
                                LocalDate.parse(postSearchRequestDTO.getEndDate()) : null),   // 검색 - 등록일자(끝)
                        approverMatchCondition(applyRequestDTO.getUserId(), docMaster.approverChain, docMaster.currentApproverIndex)
                )
                .fetchOne();

        return new PageImpl<>(resultList, page, count);
    }

    @Override
    public Page<DocPendingResponseDTO> getMyDocPendingList2(ApplyRequestDTO applyRequestDTO, Pageable page) {
        String instNm = stdBcdService.getInstNm(applyRequestDTO.getInstCd());

        List<DocPendingResponseDTO> resultSet = queryFactory.select(
                        Projections.constructor(
                                DocPendingResponseDTO.class,
                                docMaster.draftId,
                                docMaster.title,
                                docMaster.instCd,
                                Expressions.constant(instNm),
                                docMaster.draftDate,
                                docMaster.drafter,
                                docMaster.updtDt,
                                docMaster.drafter,
                                docMaster.status,
                                Expressions.stringTemplate("case when {0} = 'A' then '문서수신' else '문서발신' end", docDetail.division),
                                docMaster.approverChain,
                                docMaster.currentApproverIndex
                        )
                )
                .from(docMaster)
                .leftJoin(docDetail).on(docMaster.draftId.eq(docDetail.draftId))
                .where(
                        docMaster.drafterId.eq(applyRequestDTO.getUserId()),
                        docMaster.status.eq("A"),
                        docMaster.currentApproverIndex.eq(0)
                )
                .orderBy(docMaster.rgstDt.desc())
                .offset(page.getOffset())
                .limit(page.getPageSize())
                .fetch();

        Long count = queryFactory
                .select(docMaster.count())
                .from(docMaster)
                .where(
                        docMaster.drafterId.eq(applyRequestDTO.getUserId()),
                        docMaster.status.eq("A"),
                        docMaster.currentApproverIndex.eq(0)
                )
                .fetchOne();

        return new PageImpl<>(resultSet, page, count);
    }

    @Override
    public Long getDocPendingCount(ApplyRequestDTO applyRequestDTO, PostSearchRequestDTO postSearchRequestDTO) {
        return queryFactory.select(docMaster.count())
                .from(docMaster)
                .where(
                        docMaster.status.eq("A"),
                        docMaster.instCd.eq(applyRequestDTO.getInstCd()),
                        this.afterStartDate(StringUtils.hasLength(postSearchRequestDTO.getStartDate()) ?
                                LocalDate.parse(postSearchRequestDTO.getStartDate()) : null),
                        this.beforeEndDate(StringUtils.hasLength(postSearchRequestDTO.getEndDate()) ?
                                LocalDate.parse(postSearchRequestDTO.getEndDate()) : null),
                        approverMatchCondition(applyRequestDTO.getUserId(), docMaster.approverChain, docMaster.currentApproverIndex)
                )
                .fetchOne();
    }

    private BooleanExpression approverMatchCondition(String userId, StringPath approverChain, NumberPath<Integer> currentApproverIndex) {
        return Expressions.booleanTemplate(
                "json_unquote(json_extract(concat('[\"', replace({0}, ', ', '\",\"'), '\"]'), concat('$[', {1}, ']'))) = {2}",
                approverChain,
                currentApproverIndex,
                userId
        );
    }

    private BooleanExpression afterStartDate(LocalDate startDate) {
        if (startDate == null) {
            return Expressions.asBoolean(true).isTrue();
        }
        LocalDateTime startDateTime = startDate.atStartOfDay();
        return docMaster.draftDate.goe(startDateTime);
    }

    private BooleanExpression beforeEndDate(LocalDate endDate) {
        if (endDate == null) {
            return Expressions.asBoolean(true).isTrue();
        }
        LocalDateTime endDateTime = endDate.atTime(LocalTime.MAX);
        return docMaster.draftDate.loe(endDateTime);
    }
}
