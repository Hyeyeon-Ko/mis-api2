package kr.or.kmi.mis.api.noti.controller;

import kr.or.kmi.mis.api.noti.service.NotificationService;
import kr.or.kmi.mis.cmm.model.response.ApiResponse;
import kr.or.kmi.mis.cmm.model.response.ResponseWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequestMapping("/api/noti")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

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

    @PutMapping("/read/{notificationId}")
    public ApiResponse<?> markNotificationAsRead(@PathVariable("notificationId") Long notificationId) {
        notificationService.markAsRead(notificationId);
        return ResponseWrapper.success();
    }


}
