package kr.or.kmi.mis.api.bcd.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.or.kmi.mis.api.bcd.model.request.BcdNotificationRequestDTO;
import kr.or.kmi.mis.api.bcd.model.request.BcdRequestDTO;
import kr.or.kmi.mis.api.bcd.model.request.BcdUpdateRequestDTO;
import kr.or.kmi.mis.api.bcd.service.BcdService;
import kr.or.kmi.mis.api.noti.service.NotificationSendService;
import kr.or.kmi.mis.cmm.model.response.ApiResponse;
import kr.or.kmi.mis.cmm.model.response.ResponseWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/bcd")
@RequiredArgsConstructor
@Tag(name = "Bcd", description = "명함신청 관련 API")
public class BcdController {

    private final BcdService bcdService;
    private final NotificationSendService notificationSendService;

    @Operation(summary = "create bcd apply", description = "유저 > 명함신청 시 사용")
    @PostMapping
    public ApiResponse<?> createBcdApply(@RequestBody BcdRequestDTO bcdRequestDTO) {
        bcdService.applyBcd(bcdRequestDTO);
        return ResponseWrapper.success();
    }

    @Operation(summary = "create bcd apply by teamLeader", description = "팀원 제외 유저 > 명함신청 시 사용")
    @PostMapping(value = "/leader")
    public ApiResponse<?> createBcdApplyByLeader(@RequestBody BcdRequestDTO bcdRequestDTO) {
        bcdService.applyBcdByLeader(bcdRequestDTO);
        return ResponseWrapper.success();
    }


    @Operation(summary = "modify bcd apply", description = "유저 > 나의 신청내역 > 승인 대기 중인 명함신청 수정 시 사용")
    @PostMapping(value = "/update")
    public ApiResponse<?> updateBcdApply(@RequestParam("draftId") String draftId, @RequestBody BcdUpdateRequestDTO bcdUpdateRequestDTO) {
        bcdService.updateBcd(draftId, bcdUpdateRequestDTO);
        return ResponseWrapper.success();
    }

    @Operation(summary = "cancel bcd apply", description = "유저 > 나의 신청내역 > 승인 대기 중인 명함신청 취소 시 사용")
    @PutMapping(value = "/{draftId}")
    public ApiResponse<?> cancelBscApply(@PathVariable("draftId") String draftId) {
        bcdService.cancelBcdApply(draftId);
        return ResponseWrapper.success();
    }

    @Operation(summary = "put status ORDERED into COMPLETED", description = "유저 > 나의 신청 내역 > 명함을 수령한 후, 수령 확인 버튼 클릭 시 사용")
    @PutMapping(value = "/completeApply")
    public ApiResponse<?> completeBcdApply(@RequestParam("draftId") String draftId) {
        bcdService.completeBcdApply(draftId);
        return ResponseWrapper.success();
    }

    @Operation(summary = "send Receipt Notification", description = "관리자 > 전체 신청내역 > 명함 수령 요청 알림 전송")
    @PostMapping("/noti")
    public ApiResponse<?> sendReceiptNotification(@RequestBody BcdNotificationRequestDTO request) {
        notificationSendService.sendBcdReceipt(request.getDraftIds());
        return ResponseWrapper.success();
    }
}
