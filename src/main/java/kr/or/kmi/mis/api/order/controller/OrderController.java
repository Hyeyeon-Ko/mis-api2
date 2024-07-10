package kr.or.kmi.mis.api.order.controller;

import jakarta.mail.MessagingException;
import kr.or.kmi.mis.api.order.model.request.OrderRequestDTO;
import kr.or.kmi.mis.api.order.model.response.OrderListResponseDTO;
import kr.or.kmi.mis.api.order.service.OrderService;
import kr.or.kmi.mis.cmm.response.ApiResponse;
import kr.or.kmi.mis.cmm.response.ResponseWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/bsc/order")
public class OrderController {

    private final OrderService orderService;

    /* 발주 목록 조회 */
    @GetMapping
    public ApiResponse<List<OrderListResponseDTO>> getOrderList() {
        return ResponseWrapper.success(orderService.getOrderList());
    }

    /* 발주 요청 */
    @PostMapping
    public ApiResponse<?> orderRequest(@RequestBody OrderRequestDTO orderRequest) throws IOException, MessagingException {
        orderService.orderRequest(orderRequest.getDraftIds());
        return ResponseWrapper.success();
    }
}
