package kr.or.kmi.mis.api.order.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.mail.MessagingException;
import kr.or.kmi.mis.api.order.model.request.OrderRequestDTO;
import kr.or.kmi.mis.api.order.model.response.EmailSettingsResponseDTO;
import kr.or.kmi.mis.api.order.model.response.OrderListResponseDTO;
import kr.or.kmi.mis.api.order.service.OrderService;
import kr.or.kmi.mis.cmm.model.response.ApiResponse;
import kr.or.kmi.mis.cmm.model.response.ResponseWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/bsc/order")
@Tag(name = "Order", description = "발주 관리 API")
public class OrderController {

    private final OrderService orderService;

    @Operation(summary = "get order list", description = "발주 목록 조회")
    @GetMapping
    public ApiResponse<List<OrderListResponseDTO>> getOrderList(String instCd) {
        return ResponseWrapper.success(orderService.getOrderList(instCd));
    }

    @Operation(summary = "order request", description = "발주 요청 -> 이메일 전송")
    @PostMapping
    public ApiResponse<?> orderRequest(@RequestBody OrderRequestDTO orderRequest) throws IOException, MessagingException, GeneralSecurityException {
        orderService.orderRequest(orderRequest);
        return ResponseWrapper.success();
    }

    @Operation(summary = "get email settings", description = "기본 이메일 설정 조회")
    @GetMapping("/email-settings")
    public ApiResponse<EmailSettingsResponseDTO> getEmailSettings() {
        return ResponseWrapper.success(orderService.getEmailSettings());
    }
}
