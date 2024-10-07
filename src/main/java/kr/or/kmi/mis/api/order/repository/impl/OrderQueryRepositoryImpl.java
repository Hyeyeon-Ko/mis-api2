package kr.or.kmi.mis.api.order.repository.impl;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import kr.or.kmi.mis.api.bcd.model.entity.BcdDetail;
import kr.or.kmi.mis.api.bcd.model.entity.BcdMaster;
import kr.or.kmi.mis.api.bcd.model.entity.QBcdDetail;
import kr.or.kmi.mis.api.bcd.model.entity.QBcdMaster;
import kr.or.kmi.mis.api.exception.EntityNotFoundException;
import kr.or.kmi.mis.api.order.model.response.OrderListResponseDTO;
import kr.or.kmi.mis.api.order.repository.OrderQueryRepository;
import kr.or.kmi.mis.api.std.service.StdBcdService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * packageName    : kr.or.kmi.mis.api.order.repository.impl
 * fileName       : OrderQueryRepositoryImpl
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
public class OrderQueryRepositoryImpl implements OrderQueryRepository {

    private final JPAQueryFactory queryFactory;
    private final QBcdMaster bcdMaster = QBcdMaster.bcdMaster;
    private final QBcdDetail bcdDetail = QBcdDetail.bcdDetail;
    private final StdBcdService stdBcdService;

    @Override
    public Page<OrderListResponseDTO> getOrderList2(String instCd, Pageable page) {

        String instNm = stdBcdService.getInstNm(instCd);

        List<OrderListResponseDTO> resultSet = queryFactory.select(
                Projections.constructor(
                        OrderListResponseDTO.class,
                        bcdMaster.draftId,
                        Expressions.constant(instNm),
                        bcdMaster.title,
                        bcdMaster.draftDate,
                        bcdMaster.respondDate,
                        bcdMaster.drafter,
                        bcdDetail.quantity
                )
            )
                .from(bcdMaster)
                .leftJoin(bcdDetail).on(bcdMaster.draftId.eq(bcdDetail.draftId))
                .where(
                        bcdMaster.status.eq("B"),
                        bcdMaster.orderDate.isNull()
                )
                .orderBy(bcdMaster.rgstDt.desc())
                .offset(page.getOffset())
                .limit(page.getPageSize())
                .fetch();

        Long count = queryFactory.select(bcdMaster.count())
                .from(bcdMaster)
                .leftJoin(bcdDetail).on(bcdMaster.draftId.eq(bcdDetail.draftId))
                .where(
                        bcdMaster.status.eq("B"),
                        bcdMaster.orderDate.isNull()
                )
                .fetchOne();

        return new PageImpl<>(resultSet, page, count);
    }
}
