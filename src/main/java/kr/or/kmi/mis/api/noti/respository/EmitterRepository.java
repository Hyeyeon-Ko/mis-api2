package kr.or.kmi.mis.api.noti.respository;

import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

public interface EmitterRepository {

    void save(Long id, SseEmitter sseEmitter);
    void deleteById(Long userId);
    SseEmitter get(Long userId);
}
