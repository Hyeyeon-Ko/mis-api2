package kr.or.kmi.mis.api.corpdoc.repository.impl;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import kr.or.kmi.mis.api.apply.model.request.ApplyRequestDTO;
import kr.or.kmi.mis.api.corpdoc.model.entity.QCorpDocDetail;
import kr.or.kmi.mis.api.corpdoc.model.entity.QCorpDocMaster;
import kr.or.kmi.mis.api.corpdoc.model.response.CorpDocMasterResponseDTO;
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
    private final StdBcdService stdBcdService;

    @Override
    public Page<CorpDocMasterResponseDTO> getCorpDocApply2(ApplyRequestDTO applyRequestDTO, PostSearchRequestDTO postSearchRequestDTO, Pageable page) {

        String instNm = stdBcdService.getInstNm(applyRequestDTO.getInstCd());
        
        // 값 뭔지 몰랑
        String docType = "";

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
                        corpDocMaster.instCd.eq(applyRequestDTO.getInstCd()),
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
                        corpDocMaster.instCd.eq(applyRequestDTO.getInstCd()),
                        this.titleContains(postSearchRequestDTO.getSearchType(), postSearchRequestDTO.getKeyword()),
                        this.afterStartDate(StringUtils.hasLength(postSearchRequestDTO.getStartDate()) ?
                                LocalDate.parse(postSearchRequestDTO.getStartDate()) : null),    // 검색 - 등록일자(시작)
                        this.beforeEndDate(StringUtils.hasLength(postSearchRequestDTO.getEndDate()) ?
                                LocalDate.parse(postSearchRequestDTO.getEndDate()) : null)   // 검색 - 등록일자(끝)
                )
                .fetchOne();

        return new PageImpl<>(resultSet, page, count);
    }

    private BooleanExpression titleContains(String searchType, String title) {
        if (StringUtils.hasLength(searchType) && StringUtils.hasLength(title)) {
            switch (searchType) {
                case "제목": return corpDocMaster.title.like("%" + title + "%");
                case "신청자": return corpDocMaster.drafter.like("%" + title + "%");
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
