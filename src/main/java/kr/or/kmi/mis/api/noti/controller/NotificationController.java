package kr.or.kmi.mis.api.noti.controller;

import kr.or.kmi.mis.api.user.service.InfoService;
import kr.or.kmi.mis.api.noti.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequestMapping("/api/noti")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;
    private final InfoService infoService;

    /**
     * @apiNote : 로그인 한 유저와의 sse 연결
     * @param userId : 사용자 ID
     *
     * fixme : infoService(그룹웨어)로 userId 값 불러오는 로직 불안정 해, 직접 유저 ID 받아오는 것으로 우선 구현
     *
     */

    @GetMapping(value = "/subscribe/{userId}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter subscribe(@PathVariable("userId") String userId) {
        return notificationService.subscribe(userId);
    }

}
