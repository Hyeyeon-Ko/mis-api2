package kr.or.kmi.mis.api.corpdoc.repository.impl;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import kr.or.kmi.mis.api.apply.model.request.ApplyRequestDTO;
import kr.or.kmi.mis.api.corpdoc.model.entity.QCorpDocDetail;
import kr.or.kmi.mis.api.corpdoc.model.entity.QCorpDocMaster;
import kr.or.kmi.mis.api.corpdoc.model.response.CorpDocIssueResponseDTO;
import kr.or.kmi.mis.api.corpdoc.model.response.CorpDocMasterResponseDTO;
import kr.or.kmi.mis.api.corpdoc.model.response.CorpDocMyResponseDTO;
import kr.or.kmi.mis.api.corpdoc.model.response.CorpDocRnpResponseDTO;
import kr.or.kmi.mis.api.corpdoc.repository.CorpDocQueryRepository;
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

/**
 * packageName    : kr.or.kmi.mis.api.corpdoc.repository.impl
 * fileName       : CorpDocQueryRepositoryImpl
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
public class CorpDocQueryRepositoryImpl implements CorpDocQueryRepository {

    private final JPAQueryFactory queryFactory;
    private final QCorpDocMaster corpDocMaster = QCorpDocMaster.corpDocMaster;
    private final QCorpDocDetail corpDocDetail = QCorpDocDetail.corpDocDetail;
    private final StdBcdService stdBcdService;

    public Page<CorpDocIssueResponseDTO> getCorpDocIssueList2(PostSearchRequestDTO postSearchRequestDTO, Pageable page) {
        List<CorpDocIssueResponseDTO> resultSet = queryFactory.select(
                Projections.constructor(
                        CorpDocIssueResponseDTO.class,
                        corpDocMaster.draftId,
                        corpDocMaster.draftDate,
                        corpDocDetail.useDate,
                        corpDocDetail.issueDate,
                        corpDocMaster.drafter,
                        corpDocMaster.instCd,
                        Expressions.constant(""),
                        corpDocMaster.status,
                        corpDocDetail.submission,
                        corpDocDetail.purpose,
                        corpDocDetail.certCorpseal,
                        corpDocDetail.totalCorpseal,
                        corpDocDetail.certCoregister,
                        corpDocDetail.totalCoregister,
                        corpDocDetail.certUsesignet,
                        corpDocDetail.warrant,
                        corpDocDetail.type
                )
        )
                .from(corpDocMaster)
                .leftJoin(corpDocDetail).on(corpDocMaster.draftId.eq(corpDocDetail.draftId))
                .where(
                        corpDocMaster.status.eq("G") // 발급완료
                                .or(corpDocMaster.status.eq("X") // 입고
                                        .or(corpDocMaster.status.eq("E"))), // 처리완료
                        this.titleContains(postSearchRequestDTO.getSearchType(), postSearchRequestDTO.getKeyword()),
                        this.afterStartDate(StringUtils.hasLength(postSearchRequestDTO.getStartDate()) ?
                                LocalDate.parse(postSearchRequestDTO.getStartDate()) : null),    // 검색 - 등록일자(시작)
                        this.beforeEndDate(StringUtils.hasLength(postSearchRequestDTO.getEndDate()) ?
                                LocalDate.parse(postSearchRequestDTO.getEndDate()) : null)   // 검색 - 등록일자(끝)
                )
                .orderBy(corpDocDetail.issueDate.asc())
                .offset(page.getOffset())
                .limit(page.getPageSize())
                .fetch();

        Long count = queryFactory.select(corpDocMaster.count())
                .from(corpDocMaster)
                .leftJoin(corpDocDetail).on(corpDocMaster.draftId.eq(corpDocDetail.draftId))
                .where(
                        corpDocMaster.status.eq("G")
                                .or(corpDocMaster.status.eq("X")
                                        .or(corpDocMaster.status.eq("E"))), // 처리완료
                        this.titleContains(postSearchRequestDTO.getSearchType(), postSearchRequestDTO.getKeyword()),
                        this.afterStartDate(StringUtils.hasLength(postSearchRequestDTO.getStartDate()) ?
                                LocalDate.parse(postSearchRequestDTO.getStartDate()) : null),    // 검색 - 등록일자(시작)
                        this.beforeEndDate(StringUtils.hasLength(postSearchRequestDTO.getEndDate()) ?
                                LocalDate.parse(postSearchRequestDTO.getEndDate()) : null)   // 검색 - 등록일자(끝)
                )
                .fetchOne();

        resultSet.forEach(dto -> {
            if (!"X".equals(dto.getStatus())) {
                String instNm = stdBcdService.getInstNm(dto.getInstCd());
                dto.setInstNm(instNm);
            }
        });

        return new PageImpl<>(resultSet, page, count);
    }

    @Override
    public Page<CorpDocIssueResponseDTO> getCorpDocIssuePendingList(Pageable page) {
        List<CorpDocIssueResponseDTO> resultSet = queryFactory.select(
                        Projections.constructor(
                                CorpDocIssueResponseDTO.class,
                                corpDocMaster.draftId,
                                corpDocMaster.draftDate,
                                corpDocDetail.useDate,
                                corpDocDetail.issueDate,
                                corpDocMaster.drafter,
                                corpDocMaster.instCd,
                                Expressions.constant(""),
                                corpDocMaster.status,
                                corpDocDetail.submission,
                                corpDocDetail.purpose,
                                corpDocDetail.certCorpseal,
                                corpDocDetail.totalCorpseal,
                                corpDocDetail.certCoregister,
                                corpDocDetail.totalCoregister,
                                corpDocDetail.certUsesignet,
                                corpDocDetail.warrant,
                                corpDocDetail.type
                        )
                )
                .from(corpDocMaster)
                .leftJoin(corpDocDetail).on(corpDocMaster.draftId.eq(corpDocDetail.draftId))
                .where(
                        corpDocMaster.status.eq("B") // 발급대기
                )
                .orderBy(corpDocMaster.draftDate.asc())
                .offset(page.getOffset())
                .limit(page.getPageSize())
                .fetch();

        Long count = queryFactory.select(corpDocMaster.count())
                .from(corpDocMaster)
                .leftJoin(corpDocDetail).on(corpDocMaster.draftId.eq(corpDocDetail.draftId))
                .where(
                        corpDocMaster.status.eq("B")
                )
                .fetchOne();

        resultSet.forEach(dto -> {
                String instNm = stdBcdService.getInstNm(dto.getInstCd());
                dto.setInstNm(instNm);
        });

        return new PageImpl<>(resultSet, page, count);
    }

    @Override
    public Page<CorpDocRnpResponseDTO> getCorpDocRnpList(String instCd, PostSearchRequestDTO postSearchRequestDTO, Pageable page) {
        List<CorpDocRnpResponseDTO> resultSet = queryFactory.select(
                        Projections.constructor(
                                CorpDocRnpResponseDTO.class,
                                corpDocMaster.draftId,
                                corpDocMaster.drafter,
                                corpDocMaster.draftDate,
                                corpDocMaster.endDate,
                                corpDocDetail.submission,
                                corpDocDetail.purpose,
                                corpDocDetail.certCorpseal,
                                corpDocDetail.certCoregister,
                                corpDocDetail.certUsesignet,
                                corpDocDetail.warrant
                        )
                )
                .from(corpDocMaster)
                .leftJoin(corpDocDetail).on(corpDocMaster.draftId.eq(corpDocDetail.draftId))
                .where(
                        corpDocMaster.status.eq("E"), // 발급대기
                        corpDocMaster.instCd.eq(instCd)
                )
                .orderBy(corpDocMaster.draftDate.asc())
                .offset(page.getOffset())
                .limit(page.getPageSize())
                .fetch();

        Long count = queryFactory.select(corpDocMaster.count())
                .from(corpDocMaster)
                .leftJoin(corpDocDetail).on(corpDocMaster.draftId.eq(corpDocDetail.draftId))
                .where(
                        corpDocMaster.status.eq("E"),
                        corpDocMaster.instCd.eq(instCd)
                )
                .fetchOne();

        return new PageImpl<>(resultSet, page, count);
    }

    @Override
    public Page<CorpDocMasterResponseDTO> getCorpDocApply2(ApplyRequestDTO applyRequestDTO, PostSearchRequestDTO postSearchRequestDTO, Pageable page) {

        String instNm = stdBcdService.getInstNm(applyRequestDTO.getInstCd());

        String docType = "법인서류";

        List<CorpDocMasterResponseDTO> resultSet = queryFactory.select(
                        Projections.constructor(
                                CorpDocMasterResponseDTO.class,
                                corpDocMaster.draftId,
                                corpDocMaster.draftDate,
                                corpDocMaster.respondDate,
                                corpDocMaster.drafter,
                                corpDocMaster.title,
                                corpDocMaster.status,
                                corpDocMaster.instCd,
                                Expressions.constant(instNm),
                                Expressions.constant(docType)
                        )
                )
                .from(corpDocMaster)
                .where(
                        corpDocMaster.status.ne("F"),
                        // corpDocMaster.instCd.eq(applyRequestDTO.getInstCd()), // 법인서류는 센터별 X !!
                        this.titleContains(postSearchRequestDTO.getSearchType(), postSearchRequestDTO.getKeyword()),
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
                        corpDocMaster.status.ne("F"),
                        //corpDocMaster.instCd.eq(applyRequestDTO.getInstCd()),
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
    public Page<CorpDocMyResponseDTO> getMyCorpDocApply2(ApplyRequestDTO applyRequestDTO, PostSearchRequestDTO postSearchRequestDTO, Pageable page) {

        String docType = "법인서류";

        List<CorpDocMyResponseDTO> resultSet = queryFactory.select(
                Projections.constructor(
                        CorpDocMyResponseDTO.class,
                        corpDocMaster.draftId,
                        corpDocMaster.title,
                        corpDocMaster.draftDate,
                        corpDocMaster.respondDate,
                        corpDocMaster.drafter,
                        corpDocMaster.approver,
                        corpDocMaster.disapprover,
                        corpDocMaster.status,
                        corpDocMaster.rejectReason,
                        Expressions.constant(docType)
                    )
                )
                .from(corpDocMaster)
                .where(
                        corpDocMaster.drafterId.eq(applyRequestDTO.getUserId()),
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
                        corpDocMaster.drafterId.eq(applyRequestDTO.getUserId()),
                        this.afterStartDate(StringUtils.hasLength(postSearchRequestDTO.getStartDate()) ?
                                LocalDate.parse(postSearchRequestDTO.getStartDate()) : null),    // 검색 - 등록일자(시작)
                        this.beforeEndDate(StringUtils.hasLength(postSearchRequestDTO.getEndDate()) ?
                                LocalDate.parse(postSearchRequestDTO.getEndDate()) : null)   // 검색 - 등록일자(끝)
                )
                .fetchOne();

        return new PageImpl<>(resultSet, page, count);
    }

    @Override
    public List<CorpDocMyResponseDTO> getMyCorpDocApply(ApplyRequestDTO applyRequestDTO, PostSearchRequestDTO postSearchRequestDTO) {
        String docType = "법인서류";

        return queryFactory.select(
                        Projections.constructor(
                                CorpDocMyResponseDTO.class,
                                corpDocMaster.draftId,
                                corpDocMaster.title,
                                corpDocMaster.draftDate,
                                corpDocMaster.respondDate,
                                corpDocMaster.drafter,
                                corpDocMaster.approver,
                                corpDocMaster.disapprover,
                                corpDocMaster.status,
                                corpDocMaster.rejectReason,
                                Expressions.constant(docType)
                        )
                )
                .from(corpDocMaster)
                .where(
                        corpDocMaster.drafterId.eq(applyRequestDTO.getUserId()),
                        this.afterStartDate(StringUtils.hasLength(postSearchRequestDTO.getStartDate()) ?
                                LocalDate.parse(postSearchRequestDTO.getStartDate()) : null),    // 검색 - 등록일자(시작)
                        this.beforeEndDate(StringUtils.hasLength(postSearchRequestDTO.getEndDate()) ?
                                LocalDate.parse(postSearchRequestDTO.getEndDate()) : null)   // 검색 - 등록일자(끝)
                )
                .orderBy(corpDocMaster.rgstDt.desc())
                .fetch();
    }

    private BooleanExpression titleContains(String searchType, String title) {
        if (StringUtils.hasLength(searchType) && StringUtils.hasLength(title)) {
            switch (searchType) {
                case "전체": return corpDocMaster.title.containsIgnoreCase(title).or(corpDocMaster.drafter.containsIgnoreCase(title));
                case "제목": return corpDocMaster.title.containsIgnoreCase(title);
                case "신청자": return corpDocMaster.drafter.containsIgnoreCase(title);
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
