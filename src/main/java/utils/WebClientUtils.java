package utils;

import kr.or.kmi.mis.config.WebClientConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

/**
 * packageName    : utils
 * fileName       : WebClientUtils
 * author         : KMI_DI
 * date           : 2024-07-12
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2024-07-12        KMI_DI       the first create
 */
@Component
@RequiredArgsConstructor
public class WebClientUtils {
    private final WebClientConfig webClientConfig;

    public <T> T get(String url, Class<T> responseDtoClass) {
        return webClientConfig.webClient().method(HttpMethod.GET)
                .uri(url)
                .retrieve()
                .onStatus(status -> status.is4xxClientError()
                                || status.is5xxServerError()
                        , clientResponse ->
                                clientResponse.bodyToMono(String.class)
                                        .map(body -> new RuntimeException(body)))
                .bodyToMono(responseDtoClass)
                .block();
    }

    public <T, V> T post(String url, V requestDto, Class<T> responseDtoClass) {
        return webClientConfig.webClient().method(HttpMethod.POST)
                .uri(url)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestDto)
                .retrieve()
                .onStatus(status -> status.is4xxClientError()
                                || status.is5xxServerError()
                        , clientResponse ->
                                clientResponse.bodyToMono(String.class)
                                        .map(body -> new RuntimeException(body)))
                .bodyToMono(responseDtoClass)
                .block();
    }
}
