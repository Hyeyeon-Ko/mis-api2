package kr.or.kmi.mis.api.order.controller;

import kr.or.kmi.mis.api.order.model.response.OrderListResponseDTO;
import kr.or.kmi.mis.api.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/order")
public class OrderController {

    private OrderService orderService;

    /* 발주 목록 조회 */
    @GetMapping("/orderList")
    public ResponseEntity<List<OrderListResponseDTO>> getOrderList() {
        List<OrderListResponseDTO> orderList = orderService.getOrderList();
        return ResponseEntity.status(HttpStatus.OK).body(orderList);
    }

    /* 발주 요청 -> 발주 요청 과정 받은 후 구현 */
    @PostMapping("/orderList/order")
    public ResponseEntity<String> order(@RequestParam("draftId") Long id) {
        return new ResponseEntity<>("ok", HttpStatus.OK);
    }
}

