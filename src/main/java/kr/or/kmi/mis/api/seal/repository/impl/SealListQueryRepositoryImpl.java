package kr.or.kmi.mis.api.seal.repository.impl;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import kr.or.kmi.mis.api.apply.model.request.ApplyRequestDTO;
import kr.or.kmi.mis.api.file.model.entity.QFileDetail;
import kr.or.kmi.mis.api.file.model.entity.QFileHistory;
import kr.or.kmi.mis.api.seal.model.entity.QSealExportDetail;
import kr.or.kmi.mis.api.seal.model.entity.QSealImprintDetail;
import kr.or.kmi.mis.api.seal.model.entity.QSealMaster;
import kr.or.kmi.mis.api.seal.model.entity.QSealRegisterDetail;
import kr.or.kmi.mis.api.seal.model.response.ExportListResponseDTO;
import kr.or.kmi.mis.api.seal.model.response.ManagementListResponseDTO;
import kr.or.kmi.mis.api.seal.model.response.RegistrationListResponseDTO;
import kr.or.kmi.mis.api.seal.model.response.TotalRegistrationListResponseDTO;
import kr.or.kmi.mis.api.seal.repository.SealListQueryRepository;
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
public class SealListQueryRepositoryImpl implements SealListQueryRepository {

    private final JPAQueryFactory queryFactory;
    private final QSealMaster sealMaster = QSealMaster.sealMaster;
    private final QSealImprintDetail sealImprintDetail = QSealImprintDetail.sealImprintDetail;
    private final QSealExportDetail sealExportDetail = QSealExportDetail.sealExportDetail;
    private final QSealRegisterDetail sealRegisterDetail = QSealRegisterDetail.sealRegisterDetail;
    private final QFileDetail fileDetail = QFileDetail.fileDetail;
    private final QFileHistory fileHistory = QFileHistory.fileHistory;

    @Override
    public Page<ManagementListResponseDTO> getSealManagementList(ApplyRequestDTO applyRequestDTO, PostSearchRequestDTO postSearchRequestDTO, Pageable page) {

        List<ManagementListResponseDTO> resultSet = queryFactory
                .select(Projections.constructor(
                        ManagementListResponseDTO.class,
                        sealMaster.draftId,
                        sealMaster.drafter,
                        sealImprintDetail.submission,
                        sealImprintDetail.useDate,
                        sealImprintDetail.corporateSeal,
                        sealImprintDetail.facsimileSeal,
                        sealImprintDetail.companySeal,
                        sealImprintDetail.purpose,
                        sealImprintDetail.notes
                ))
                .from(sealMaster)
                .leftJoin(sealImprintDetail).on(sealMaster.draftId.eq(sealImprintDetail.draftId))
                .where(
                        sealMaster.status.eq("E"),
                        sealMaster.division.eq("A"),
                        sealMaster.instCd.eq(applyRequestDTO.getInstCd()),
                        this.containsImprintKeyword(postSearchRequestDTO.getSearchType(), postSearchRequestDTO.getKeyword()),
                        this.afterUseDateStartDate(StringUtils.hasLength(postSearchRequestDTO.getStartDate()) ?
                                LocalDate.parse(postSearchRequestDTO.getStartDate()) : null),    // 검색 - 등록일자(시작)
                        this.beforeUseDateEndDate(StringUtils.hasLength(postSearchRequestDTO.getEndDate()) ?
                                LocalDate.parse(postSearchRequestDTO.getEndDate()) : null)   // 검색 - 등록일자(끝)
                )
                .orderBy(sealMaster.rgstDt.desc())
                .offset(page.getOffset())
                .limit(page.getPageSize())
                .fetch();

        Long count = queryFactory
                .select(sealMaster.count())
                .from(sealMaster)
                .leftJoin(sealImprintDetail).on(sealMaster.draftId.eq(sealImprintDetail.draftId))
                .where(
                        sealMaster.status.eq("E"),
                        sealMaster.division.eq("A"),
                        sealMaster.instCd.eq(applyRequestDTO.getInstCd()),
                        this.containsImprintKeyword(postSearchRequestDTO.getSearchType(), postSearchRequestDTO.getKeyword()),
                        this.afterUseDateStartDate(StringUtils.hasLength(postSearchRequestDTO.getStartDate()) ?
                                LocalDate.parse(postSearchRequestDTO.getStartDate()) : null),    // 검색 - 등록일자(시작)
                        this.beforeUseDateEndDate(StringUtils.hasLength(postSearchRequestDTO.getEndDate()) ?
                                LocalDate.parse(postSearchRequestDTO.getEndDate()) : null)   // 검색 - 등록일자(끝)
                )
                .fetchOne();

        return new PageImpl<>(resultSet, page, count);
    }

    @Override
    public Page<ExportListResponseDTO> getSealExportList(ApplyRequestDTO applyRequestDTO, PostSearchRequestDTO postSearchRequestDTO, Pageable pageable) {

        List<ExportListResponseDTO> resultList = queryFactory
                .select(Projections.constructor(
                        ExportListResponseDTO.class,
                        sealExportDetail.draftId,
                        sealMaster.drafter,
                        sealExportDetail.submission,
                        sealExportDetail.expNm,
                        sealExportDetail.expDate,
                        sealExportDetail.returnDate,
                        sealExportDetail.corporateSeal,
                        sealExportDetail.facsimileSeal,
                        sealExportDetail.companySeal,
                        sealExportDetail.purpose,
                        sealExportDetail.notes,
                        fileHistory.fileName,
                        fileHistory.filePath
                ))
                .from(sealExportDetail)
                .leftJoin(sealMaster).on(sealExportDetail.draftId.eq(sealMaster.draftId))
                .leftJoin(fileDetail).on(sealExportDetail.draftId.eq(fileDetail.draftId))
                .leftJoin(fileHistory).on(fileDetail.attachId.eq(fileHistory.attachId))
                .where(
                        sealMaster.status.eq("E"),
                        sealMaster.division.eq("B"),
                        sealMaster.instCd.eq(applyRequestDTO.getInstCd()),
                        this.containsExportKeyword(postSearchRequestDTO.getSearchType(), postSearchRequestDTO.getKeyword()),
                        this.afterExportDateStartDate(StringUtils.hasLength(postSearchRequestDTO.getStartDate()) ?
                                LocalDate.parse(postSearchRequestDTO.getStartDate()) : null),    // 검색 - 등록일자(시작)
                        this.beforeExportDateEndDate(StringUtils.hasLength(postSearchRequestDTO.getEndDate()) ?
                                LocalDate.parse(postSearchRequestDTO.getEndDate()) : null)   // 검색 - 등록일자(끝)
                )
                .orderBy(sealExportDetail.rgstDt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long count = queryFactory
                .select(sealExportDetail.count())
                .from(sealExportDetail)
                .leftJoin(sealMaster).on(sealExportDetail.draftId.eq(sealMaster.draftId))
                .where(
                        sealMaster.status.eq("E"),
                        sealMaster.division.eq("B"),
                        sealMaster.instCd.eq(applyRequestDTO.getInstCd()),
                        this.containsExportKeyword(postSearchRequestDTO.getSearchType(), postSearchRequestDTO.getKeyword()),
                        this.afterExportDateStartDate(StringUtils.hasLength(postSearchRequestDTO.getStartDate()) ?
                                LocalDate.parse(postSearchRequestDTO.getStartDate()) : null),    // 검색 - 등록일자(시작)
                        this.beforeExportDateEndDate(StringUtils.hasLength(postSearchRequestDTO.getEndDate()) ?
                                LocalDate.parse(postSearchRequestDTO.getEndDate()) : null)   // 검색 - 등록일자(끝)
                )
                .fetchOne();

        return new PageImpl<>(resultList, pageable, count);
    }

    @Override
    public Page<RegistrationListResponseDTO> getRegistrationList(ApplyRequestDTO applyRequestDTO, Pageable page) {

        List<RegistrationListResponseDTO> resultSet = queryFactory.select(
                        Projections.constructor(
                                RegistrationListResponseDTO.class,
                                sealRegisterDetail.draftId,
                                sealRegisterDetail.sealNm,
                                sealRegisterDetail.sealImage,
                                sealRegisterDetail.useDept,
                                sealRegisterDetail.purpose,
                                sealRegisterDetail.manager,
                                sealRegisterDetail.subManager,
                                sealRegisterDetail.draftDate
                        )
                )
                .from(sealRegisterDetail)
                .where(
                        sealRegisterDetail.deletedt.isNull(),
                        sealRegisterDetail.instCd.eq(applyRequestDTO.getInstCd())
                )
                .orderBy(sealRegisterDetail.rgstDt.desc())
                .offset(page.getOffset())
                .limit(page.getPageSize())
                .fetch();

        Long count = queryFactory.select(sealRegisterDetail.count())
                .from(sealRegisterDetail)
                .where(
                        sealRegisterDetail.deletedt.isNull(),
                        sealRegisterDetail.instCd.eq(applyRequestDTO.getInstCd())
                )
                .fetchOne();

        return new PageImpl<>(resultSet, page, count);
    }

    @Override
    public Page<TotalRegistrationListResponseDTO> getTotalSealRegistrationList(Pageable page) {

        List<TotalRegistrationListResponseDTO> resultSet = queryFactory.select(
                        Projections.constructor(
                                TotalRegistrationListResponseDTO.class,
                                sealRegisterDetail.draftId,
                                sealRegisterDetail.sealNm,
                                sealRegisterDetail.sealImage,
                                sealRegisterDetail.useDept,
                                sealRegisterDetail.purpose,
                                sealRegisterDetail.manager,
                                sealRegisterDetail.subManager,
                                sealRegisterDetail.draftDate,
                                sealRegisterDetail.instCd
                        )
                )
                .from(sealRegisterDetail)
                .where(
                        sealRegisterDetail.deletedt.isNull()
                )
                .orderBy(sealRegisterDetail.rgstDt.desc())
                .offset(page.getOffset())
                .limit(page.getPageSize())
                .fetch();

        Long count = queryFactory.select(sealRegisterDetail.count())
                .from(sealRegisterDetail)
                .where(
                        sealRegisterDetail.deletedt.isNull()
                )
                .fetchOne();

        return new PageImpl<>(resultSet, page, count);
    }

    private BooleanExpression containsImprintKeyword(String searchType, String keyword) {
        if (StringUtils.hasLength(searchType) && StringUtils.hasLength(keyword)) {
            switch (searchType) {
                case "일자": return sealImprintDetail.useDate.containsIgnoreCase(keyword);
                case "제출처": return sealImprintDetail.submission.containsIgnoreCase(keyword);
                case "사용목적": return sealImprintDetail.purpose.containsIgnoreCase(keyword);
                case "전체": return sealImprintDetail.useDate.containsIgnoreCase(keyword)
                        .or(sealImprintDetail.submission.containsIgnoreCase(keyword))
                        .or(sealImprintDetail.purpose.containsIgnoreCase(keyword));
                default: return null;
            }
        }
        return null;
    }

    private BooleanExpression containsExportKeyword(String searchType, String keyword) {
        if (StringUtils.hasLength(searchType) && StringUtils.hasLength(keyword)) {
            switch (searchType) {
                case "반출일자": return sealExportDetail.expDate.like("%" + keyword + "%");
                case "반납일자": return sealExportDetail.submission.like("%" + keyword + "%");
                case "사용목적": return sealExportDetail.purpose.like("%" + keyword + "%");
                case "전체": return sealExportDetail.expDate.contains(keyword)
                        .or(sealExportDetail.submission.contains(keyword))
                        .or(sealExportDetail.purpose.contains(keyword));
                default: return null;
            }
        }
        return null;
    }

    private BooleanExpression afterUseDateStartDate(LocalDate startDate) {
        if (startDate == null) {
            return Expressions.asBoolean(true).isTrue();
        }
        LocalDateTime startDateTime = startDate.atStartOfDay();
        return sealImprintDetail.useDate.goe(String.valueOf(startDateTime));
    }

    private BooleanExpression beforeUseDateEndDate(LocalDate endDate) {
        if (endDate == null) {
            return Expressions.asBoolean(true).isTrue();
        }
        LocalDateTime endDateTime = endDate.atTime(LocalTime.MAX);
        return sealImprintDetail.useDate.loe(String.valueOf(endDateTime));
    }

    private BooleanExpression afterExportDateStartDate(LocalDate startDate) {
        if (startDate == null) {
            return Expressions.asBoolean(true).isTrue();
        }
        LocalDateTime startDateTime = startDate.atStartOfDay();
        return sealExportDetail.expDate.goe(String.valueOf(startDateTime));
    }

    private BooleanExpression beforeExportDateEndDate(LocalDate endDate) {
        if (endDate == null) {
            return Expressions.asBoolean(true).isTrue();
        }
        LocalDateTime endDateTime = endDate.atTime(LocalTime.MAX);
        return sealExportDetail.expDate.loe(String.valueOf(endDateTime));
    }
}
