package kr.or.kmi.mis.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
/**
 * packageName    : kr.or.kmi.mis.config
 * fileName       : CorsConfig
 * author         : KMI_DI
 * date           : 2024-07-02
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2024-07-02        KMI_DI       the first create
 */
@Configuration
public class CorsConfig {
    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")
                        .allowedOriginPatterns("http://localhost:3000")  // 로컬주소(개발진행시)
//                        .allowedOriginPatterns("http://xxx.xxx.xxx")  // 운영서버 주소
//                        .allowedOriginPatterns("http://xxx.xxx.xxx:xxx")  // 개발서버 주소
                        .allowedMethods("GET", "POST", "PUT", "DELETE")
                        .allowedHeaders("Origin", "Content-Type", "Accept", "Authorization")
                        .exposedHeaders("Authorization");
            }
        };
    }
}
