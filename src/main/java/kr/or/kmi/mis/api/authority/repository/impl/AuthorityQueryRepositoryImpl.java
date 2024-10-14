package kr.or.kmi.mis.api.authority.repository.impl;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import kr.or.kmi.mis.api.authority.model.entity.QAuthority;
import kr.or.kmi.mis.api.authority.model.response.AuthorityResponseDTO2;
import kr.or.kmi.mis.api.authority.repository.AuthorityQueryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
@Slf4j
public class AuthorityQueryRepositoryImpl implements AuthorityQueryRepository {

    private final JPAQueryFactory queryFactory;
    private final QAuthority authority = QAuthority.authority;

    @Override
    public Page<AuthorityResponseDTO2> getAuthorityList(Pageable page) {
        List<AuthorityResponseDTO2> resultSet = queryFactory.select(
                Projections.constructor(
                        AuthorityResponseDTO2.class,
                        authority.authId,
                        authority.userId,
                        authority.userNm,
                        authority.role,
                        Expressions.stringTemplate("function('fn_getCodeNm', {0}, {1})", "A001", authority.instCd),
                        Expressions.stringTemplate("function('fn_getCodeNm', {0}, {1})", "A002", authority.deptCd),
                        authority.email
                )
        )
                .from(authority)
                .orderBy(authority.userNm.asc())
                .offset(page.getOffset())
                .limit(page.getPageSize())
                .fetch()
                ;

    Long count = queryFactory.select(authority.count())
            .from(authority)
            .fetchOne();

    return new PageImpl<>(resultSet, page, count);

    }
}
