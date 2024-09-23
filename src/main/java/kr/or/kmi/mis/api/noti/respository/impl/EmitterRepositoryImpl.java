package kr.or.kmi.mis.api.noti.respository.impl;

import kr.or.kmi.mis.api.noti.respository.EmitterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Repository
@RequiredArgsConstructor
public class EmitterRepositoryImpl implements EmitterRepository {

    private final Map<Long, SseEmitter> emitters = new ConcurrentHashMap<>();

    @Override
    public void save(Long userId, SseEmitter sseEmitter) {
        emitters.put(userId, sseEmitter);
    }

    @Override
    public void deleteById(Long userId) {
        emitters.remove(userId);
    }

    @Override
    public SseEmitter get(Long userId) {
        return emitters.get(userId);
    }

}
