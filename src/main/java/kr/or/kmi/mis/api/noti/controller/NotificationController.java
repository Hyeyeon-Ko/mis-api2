package kr.or.kmi.mis.api.noti.controller;

import io.swagger.v3.oas.annotations.Operation;
import kr.or.kmi.mis.api.noti.model.response.NotiResponseDTO;
import kr.or.kmi.mis.api.noti.service.NotificationService;
import kr.or.kmi.mis.cmm.model.response.ApiResponse;
import kr.or.kmi.mis.cmm.model.response.ResponseWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

//    @Operation(method = "sse 연결", description = "사용자 로그인 시, 실시간 알림을 받기 위해 sse 연결")
//    @GetMapping(value = "/subscribe/{userId}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
//    public SseEmitter subscribe(@PathVariable("userId") String userId) {
//        return notificationService.subscribe(userId);
//    }

    @Operation(method = "알림 데이터 호출", description = "DB에 저장된 알림 데이터 모두 호출")
    @GetMapping(value = "/{userId}")
    public ApiResponse<List<NotiResponseDTO>> getAllNotification(@PathVariable("userId") String userId) {
        return ResponseWrapper.success(notificationService.getAllNotification(userId));
    }

    @Operation(method = "알림 읽음 처리", description = "사용자가 알림을 읽었을 때, 읽음 처리")
    @PutMapping("/read/{notificationId}")
    public ApiResponse<?> markNotificationAsRead(@PathVariable("notificationId") Long notificationId) {
        notificationService.markAsRead(notificationId);
        return ResponseWrapper.success();
    }

    @Operation(method = "알림 모두 읽음 처리", description = "사용자가 모두읽음 버튼을 눌렀을 때, 모두 읽음 처리")
    @PutMapping("/allRead")
    public ApiResponse<?> markAllNotificationAsRead() {
        notificationService.markAllAsRead();
        return ResponseWrapper.success();
    }

    @Operation(method = "알림 개수 호출", description = "읽지 않은 알림 개수 호출")
    @GetMapping(value = "/unread/{userId}")
    public ApiResponse<?> getUnreadNotificationNum(@PathVariable("userId") String userId) {
        return ResponseWrapper.success(notificationService.getUnreadNotificationNum(userId));
    }
}
