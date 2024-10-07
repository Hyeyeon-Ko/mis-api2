package kr.or.kmi.mis.api.doc.repository.impl;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.StringTemplate;
import com.querydsl.jpa.impl.JPAQueryFactory;
import kr.or.kmi.mis.api.apply.model.request.ApplyRequestDTO;
import kr.or.kmi.mis.api.bcd.model.response.BcdMasterResponseDTO;
import kr.or.kmi.mis.api.bcd.model.response.BcdMyResponseDTO;
import kr.or.kmi.mis.api.doc.model.entity.DocDetail;
import kr.or.kmi.mis.api.doc.model.entity.DocMaster;
import kr.or.kmi.mis.api.doc.model.entity.QDocDetail;
import kr.or.kmi.mis.api.doc.model.entity.QDocMaster;
import kr.or.kmi.mis.api.doc.model.response.DocMasterResponseDTO;
import kr.or.kmi.mis.api.doc.model.response.DocMyResponseDTO;
import kr.or.kmi.mis.api.doc.repository.DocApplyQueryRepository;
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
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * packageName    : kr.or.kmi.mis.api.doc.repository.impl
 * fileName       : DocApplyQueryRepositoryImpl
 * author         : KMI_DI
 * date           : 2024-10-02
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2024-10-02        KMI_DI       the first create
 */
@Repository
@RequiredArgsConstructor
public class DocApplyQueryRepositoryImpl implements DocApplyQueryRepository {

    private final JPAQueryFactory queryFactory;
    private final QDocMaster docMaster = QDocMaster.docMaster;
    private final QDocDetail docDetail = QDocDetail.docDetail;
    private final InfoService infoService;


    @Override
    public Page<DocMasterResponseDTO> getDocApply2(ApplyRequestDTO applyRequestDTO, PostSearchRequestDTO postSearchRequestDTO, Pageable page) {

        List<DocMasterResponseDTO> resultSet = queryFactory.select(
                        Projections.constructor(
                                DocMasterResponseDTO.class,
                                docMaster.draftId,
                                docMaster.title,
                                docMaster.instCd,
                                Expressions.stringTemplate("function('fn_getCodeNm', {0}, {1})", "A001", docMaster.instCd),
                                docMaster.draftDate,
                                docMaster.respondDate,
                                docMaster.drafter,
                                docMaster.approver,
                                docDetail.division,
                                Expressions.stringTemplate("case when {0} = 'A' then '문서수신' else '문서발신' end", docDetail.division),
                                docMaster.approverChain,
                                docMaster.currentApproverIndex
                        )
                    )
                    .from(docMaster)
                    .leftJoin(docDetail).on(docMaster.draftId.eq(docDetail.draftId))
                    .where(
                            docMaster.status.ne("F"),
                            docMaster.instCd.eq(applyRequestDTO.getInstCd()),
                            // 권한에 따라 전체 조회 달라짐

                            this.titleContains(postSearchRequestDTO.getSearchType(), postSearchRequestDTO.getKeyword()),
                            this.afterStartDate(StringUtils.hasLength(postSearchRequestDTO.getStartDate()) ?
                                    LocalDate.parse(postSearchRequestDTO.getStartDate()) : null),    // 검색 - 등록일자(시작)
                            this.beforeEndDate(StringUtils.hasLength(postSearchRequestDTO.getEndDate()) ?
                                    LocalDate.parse(postSearchRequestDTO.getEndDate()) : null)   // 검색 - 등록일자(끝)
                    )
                    .orderBy(docMaster.rgstDt.desc())
                    .offset(page.getOffset())
                    .limit(page.getPageSize())
                    .fetch();

//        List<DocMasterResponseDTO> resultSet = resultList.stream()
//                .filter(dto -> {
//                    if (dto.getApplyStatus().equals("A")) {
//                        String[] approverChainArray = dto.getApproverChain().split(", ");
//                        int currentIndex = dto.getCurrentApproverIndex();
//                        return currentIndex < approverChainArray.length && approverChainArray[currentIndex].equals(applyRequestDTO.getUserId());
//                    }
//                    // 조건에 해당하지 않는 경우 포함하지 않음
//                    return false;
//                })
//                .collect(Collectors.toList());

        Long count = queryFactory.select(docMaster.count())
                .from(docMaster)
                .leftJoin(docDetail).on(docMaster.draftId.eq(docDetail.draftId))
                .where(
                        docMaster.status.ne("F"),
                        docMaster.instCd.eq(applyRequestDTO.getInstCd()),
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
    public Page<DocMyResponseDTO> getMyDocApply2(ApplyRequestDTO applyRequestDTO, PostSearchRequestDTO postSearchRequestDTO, Pageable page) {

        List<Tuple> results = queryFactory.select(docMaster, docDetail.division)
                .from(docMaster)
                .leftJoin(docDetail).on(docMaster.draftId.eq(docDetail.draftId))
                .where(
                        this.afterStartDate(StringUtils.hasLength(postSearchRequestDTO.getStartDate()) ?
                                LocalDate.parse(postSearchRequestDTO.getStartDate()) : null),    // 검색 - 등록일자(시작)
                        this.beforeEndDate(StringUtils.hasLength(postSearchRequestDTO.getEndDate()) ?
                                LocalDate.parse(postSearchRequestDTO.getEndDate()) : null)   // 검색 - 등록일자(끝)
                )
                .orderBy(docMaster.rgstDt.desc())
                .offset(page.getOffset())
                .limit(page.getPageSize())
                .fetch();

        // Map the result into DocMyResponseDTO
        List<DocMyResponseDTO> resultSet = results.stream()
                .map(tuple -> {
                    DocMaster master = tuple.get(docMaster);
                    String division = tuple.get(docDetail.division);

                    return DocMyResponseDTO.of(master, division, infoService);
                })
                .collect(Collectors.toList());

        // Count the total number of results
        Long count = queryFactory.select(docMaster.count())
                .from(docMaster)
                .where(
                        this.afterStartDate(StringUtils.hasLength(postSearchRequestDTO.getStartDate()) ?
                                LocalDate.parse(postSearchRequestDTO.getStartDate()) : null),    // 검색 - 등록일자(시작)
                        this.beforeEndDate(StringUtils.hasLength(postSearchRequestDTO.getEndDate()) ?
                                LocalDate.parse(postSearchRequestDTO.getEndDate()) : null)   // 검색 - 등록일자(끝)
                )
                .fetchOne();

        return new PageImpl<>(resultSet, page, count);

//        List<DocMaster> docMasters = queryFactory.select(docMaster)
//                .from(docMaster)
//                .where(
//
//                        this.afterStartDate(StringUtils.hasLength(postSearchRequestDTO.getStartDate()) ?
//                                LocalDate.parse(postSearchRequestDTO.getStartDate()) : null),    // 검색 - 등록일자(시작)
//                        this.beforeEndDate(StringUtils.hasLength(postSearchRequestDTO.getEndDate()) ?
//                                LocalDate.parse(postSearchRequestDTO.getEndDate()) : null)   // 검색 - 등록일자(끝)
//                )
//                .orderBy(docMaster.rgstDt.desc())
//                .offset(page.getOffset())
//                .limit(page.getPageSize())
//                .fetch();
//
//        List<DocMyResponseDTO> resultSet = docMasters.stream()
//                .map(docMaster -> DocMyResponseDTO.of(docMaster, "", infoService))
//                .collect(Collectors.toList());
//
//        Long count = queryFactory.select(docMaster.count())
//                .from(docMaster)
//                .where(
//                        this.afterStartDate(StringUtils.hasLength(postSearchRequestDTO.getStartDate()) ?
//                                LocalDate.parse(postSearchRequestDTO.getStartDate()) : null),    // 검색 - 등록일자(시작)
//                        this.beforeEndDate(StringUtils.hasLength(postSearchRequestDTO.getEndDate()) ?
//                                LocalDate.parse(postSearchRequestDTO.getEndDate()) : null)   // 검색 - 등록일자(끝)
//                )
//                .orderBy(docMaster.rgstDt.desc())
//                .fetchOne();

//        return new PageImpl<>(resultSet, page, count);
    }

    @Override
    public List<DocMyResponseDTO> getMyDocMasterList2(ApplyRequestDTO applyRequestDTO, PostSearchRequestDTO postSearchRequestDTO) {

        List<Tuple> results = queryFactory.select(docMaster, docDetail.division)
                .from(docMaster)
                .leftJoin(docDetail).on(docMaster.draftId.eq(docDetail.draftId))
                .where(
                        docMaster.drafterId.eq(applyRequestDTO.getUserId()),
                        this.afterStartDate(StringUtils.hasLength(postSearchRequestDTO.getStartDate()) ?
                                LocalDate.parse(postSearchRequestDTO.getStartDate()) : null),    // 검색 - 등록일자(시작)
                        this.beforeEndDate(StringUtils.hasLength(postSearchRequestDTO.getEndDate()) ?
                                LocalDate.parse(postSearchRequestDTO.getEndDate()) : null)   // 검색 - 등록일자(끝)
                )
                .orderBy(docMaster.rgstDt.desc())
                .fetch();

        // Map the result into DocMyResponseDTO
        List<DocMyResponseDTO> resultSet = results.stream()
                .map(tuple -> {
                    DocMaster master = tuple.get(docMaster);
                    String division = tuple.get(docDetail.division);

                    return DocMyResponseDTO.of(master, division, infoService);
                })
                .collect(Collectors.toList());

        return resultSet;
    }

    private BooleanExpression titleContains(String searchType, String title) {
        if (StringUtils.hasLength(searchType) && StringUtils.hasLength(title)) {
            switch (searchType) {
                case "전체": return docMaster.title.containsIgnoreCase(title).or(docMaster.drafter.containsIgnoreCase(title));
                case "제목": return docMaster.title.containsIgnoreCase(title);
                case "신청자": return docMaster.drafter.containsIgnoreCase(title);
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

    private BooleanExpression checkApproval(String status, String approverChain, int currentApproverIndex, String userId) {
        // status가 "A"인지 확인
        if (!"A".equals(status)) {
            return Expressions.asBoolean(true).isTrue(); // 기본적으로 조건을 무시
        }

        // approverChain을 ", "로 분리
        StringTemplate approverArray = Expressions.stringTemplate("split({0}, ', ')", approverChain);

        // currentApproverIndex가 array의 길이보다 크거나, 현재 approver가 userId와 일치하는지 확인
        return Expressions.booleanTemplate("case when {0} >= array_length({1}, 1) or {1}[{0} + 1] <> {2} then false else true end",
                currentApproverIndex, approverArray, userId);
    }

}
