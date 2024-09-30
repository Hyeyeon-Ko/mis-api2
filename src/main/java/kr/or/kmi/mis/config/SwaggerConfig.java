package kr.or.kmi.mis.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * packageName    : kr.or.kmi.mis.config
 * fileName       : SwaggerConfig
 * author         : clsung
 * date           : 2024-07-02
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2024-07-02        clsung       the first create
 */
@Configuration
public class SwaggerConfig {
    /**
     * Open api open api.
     *
     * @return the open api
     */
    @Bean
    public OpenAPI openAPI(){
        Info info = new Info()
                .title("Spring Boot 를 이용한 API 확인 Swagger")
                .version("0.1")
                .description("RESTfull API 문서입니다. \n 총무팀 프로젝트 API 입니다.");
        return new OpenAPI()
                .components(new Components())
                .info(info);
    }
}
