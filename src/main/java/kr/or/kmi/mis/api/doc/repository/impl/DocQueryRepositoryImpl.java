package kr.or.kmi.mis.api.doc.repository.impl;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import kr.or.kmi.mis.api.doc.model.entity.QDocDetail;
import kr.or.kmi.mis.api.doc.model.entity.QDocMaster;
import kr.or.kmi.mis.api.doc.model.request.DocRequestDTO;
import kr.or.kmi.mis.api.doc.model.response.DocResponseDTO;
import kr.or.kmi.mis.api.doc.repository.DocQueryRepository;
import kr.or.kmi.mis.api.file.model.entity.QFileDetail;
import kr.or.kmi.mis.api.file.model.entity.QFileHistory;
import kr.or.kmi.mis.api.std.service.StdBcdService;
import kr.or.kmi.mis.cmm.model.request.PostSearchRequestDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
 * packageName    : kr.or.kmi.mis.api.doc.repository.impl
 * fileName       : DocReceiveQueryRepositoryImpl
 * author         : KMI_DI
 * date           : 2024-10-07
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2024-10-07        KMI_DI       the first create
 */
@Repository
@RequiredArgsConstructor
@Slf4j
public class DocQueryRepositoryImpl implements DocQueryRepository {

    private final QDocMaster docMaster = QDocMaster.docMaster;
    private final QDocDetail docDetail = QDocDetail.docDetail;
    private final QFileDetail fileDetail = QFileDetail.fileDetail;
    private final QFileHistory fileHistory = QFileHistory.fileHistory;
    private final JPAQueryFactory queryFactory;

    @Override
    public Page<DocResponseDTO> getDocList(DocRequestDTO docRequestDTO, PostSearchRequestDTO postSearchRequestDTO, Pageable page) {

        List<DocResponseDTO> resultSet = queryFactory.select(
                Projections.constructor(
                        DocResponseDTO.class,
                        docMaster.draftId,
                        docMaster.draftDate,
                        docMaster.drafter,
                        docDetail.docId,
                        docDetail.receiver,
                        docMaster.title,
                        Expressions.stringTemplate("function('fn_getCodeNm', {0}, {1})", "A005", docMaster.status),
                        fileHistory.fileName,
                        fileHistory.filePath
                )
            )
                .from(docMaster)
                .innerJoin(docDetail).on(docMaster.draftId.eq(docDetail.draftId))
                .innerJoin(fileDetail).on(docMaster.draftId.eq(fileDetail.draftId))
                .innerJoin(fileHistory).on(fileDetail.attachId.eq(fileHistory.attachId))
                .where(
                        docMaster.instCd.eq(docRequestDTO.getInstCd()),
                        docDetail.docId.isNotNull(),
                        docDetail.division.eq(docRequestDTO.getStatus()),
                        this.titleContains(postSearchRequestDTO.getSearchType(), postSearchRequestDTO.getKeyword()),
                        this.afterStartDate(StringUtils.hasLength(postSearchRequestDTO.getStartDate()) ?
                                LocalDate.parse(postSearchRequestDTO.getStartDate()) : null),    // 검색 - 등록일자(시작)
                        this.beforeEndDate(StringUtils.hasLength(postSearchRequestDTO.getEndDate()) ?
                                LocalDate.parse(postSearchRequestDTO.getEndDate()) : null)   // 검색 - 등록일자(끝)
                )
                .orderBy(docDetail.docId.asc())
                .offset(page.getOffset())
                .limit(page.getPageSize())
                .fetch();

        Long count = queryFactory.select(docMaster.count()
                )
                .from(docMaster)
                .innerJoin(docDetail).on(docMaster.draftId.eq(docDetail.draftId))
                .innerJoin(fileDetail).on(docMaster.draftId.eq(fileDetail.draftId))
                .innerJoin(fileHistory).on(fileDetail.attachId.eq(fileHistory.attachId))
                .where(
                        docMaster.instCd.eq(docRequestDTO.getInstCd()),
                        docDetail.docId.isNotNull(),
                        docDetail.division.eq(docRequestDTO.getStatus()),
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
                case "전체": return docMaster.title.containsIgnoreCase(title)
                        .or(docDetail.docTitle.containsIgnoreCase(title))
                        .or(docDetail.receiver.containsIgnoreCase(title))
                        .or(docDetail.sender.containsIgnoreCase(title))
                        .or(docMaster.drafter.containsIgnoreCase(title));
                case "제목": return docDetail.docTitle.containsIgnoreCase(title);
                case "수신처": return docDetail.receiver.containsIgnoreCase(title);
                case "발신처": return docDetail.sender.containsIgnoreCase(title);
                case "접수인": return docMaster.drafter.containsIgnoreCase(title);
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
