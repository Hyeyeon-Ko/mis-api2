package kr.or.kmi.mis.api.noti.service.impl;

import kr.or.kmi.mis.api.noti.respository.EmitterRepository;
import kr.or.kmi.mis.api.noti.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationServiceImpl implements NotificationService {

    private final EmitterRepository emitterRepository;

    private static final Long DEFAULT_TIMEOUT = 60L * 1000 * 60;

    @Override
    @Transactional
    public SseEmitter subscribe(String userId) {
        log.info("Subscribing to user {}", userId);
        Long id = Long.parseLong(userId);
        SseEmitter emitter = createEmitter(id);

        sendToClient(id, "EventStream Created. [userId="+ id + "]", "sse 접속 성공");
        return emitter;
    }

    @Override
    @Transactional
    public <T> void customNotify(Long userId, T data, String comment) {
        log.info("Custom notify user {}", userId);
        sendToClient(userId, data, comment);
    }

    private SseEmitter createEmitter(Long userId) {
        SseEmitter emitter = new SseEmitter(DEFAULT_TIMEOUT);
        emitterRepository.save(userId, emitter);

        emitter.onCompletion(() -> emitterRepository.deleteById(userId));
        emitter.onTimeout(() -> emitterRepository.deleteById(userId));

        return emitter;
    }

    private <T> void sendToClient(Long userId, T data, String comment) {
        log.info("sendToClinet 진입 >>>> Custom notify user {}", userId);
        SseEmitter emitter = emitterRepository.get(userId);
        if (emitter != null) {
            log.info("emitter : {}", emitter);
            try {
                emitter.send(SseEmitter.event()
                        .id(String.valueOf(userId))
                        .name("notification")
                        .data(data)
                        .comment(comment));
            } catch (IOException e) {
                emitterRepository.deleteById(userId);
                emitter.completeWithError(e);
            }
        }
    }
}
