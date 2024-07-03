package kr.or.kmi.mis.config;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * packageName    : kr.or.kmi.mis.config
 * fileName       : QueryConfig
 * author         : clsung
 * date           : 2024-07-02
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2024-07-02        clsung       the first create
 */
@Configuration
public class QueryDslConfig {
    @PersistenceContext
    private EntityManager entityManager;

    /**
     * Jpa query factory jpa query factory.
     *
     * @return the jpa query factory
     */
    @Bean
    public JPAQueryFactory jpaQueryFactory(){
        return new JPAQueryFactory(entityManager);
    }

}
