package kr.or.kmi.mis.api.toner.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.mail.MessagingException;
import kr.or.kmi.mis.api.order.model.request.OrderRequestDTO;
import kr.or.kmi.mis.api.order.model.response.EmailSettingsResponseDTO;
import kr.or.kmi.mis.api.toner.model.response.TonerOrderResponseDTO;
import kr.or.kmi.mis.api.toner.service.TonerOrderService;
import kr.or.kmi.mis.cmm.model.response.ApiResponse;
import kr.or.kmi.mis.cmm.model.response.ResponseWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/toner/order")
@Tag(name = "Toner OrderList", description = "토너 발주내역 관련 API")
public class TonerOrderController {

    private final TonerOrderService tonerOrderService;

    @Operation(summary = "get Toner Order List", description = "센터별 토너 발주 목록 조회")
    @GetMapping
    public ApiResponse<List<TonerOrderResponseDTO>> getTonerOrderList(String instCd){
        return ResponseWrapper.success(tonerOrderService.getTonerOrderList(instCd));
    }

    @Operation(summary = "order Toner", description = "토너 발주 요청 -> 이메일 전송")
    @PostMapping
    public ApiResponse<?> orderToner(@RequestBody OrderRequestDTO orderRequestDTO) throws IOException, MessagingException, GeneralSecurityException {
        tonerOrderService.orderToner(orderRequestDTO);
        return ResponseWrapper.success();
    }

    @Operation(summary = "get email settings", description = "기본 이메일 설정 조회")
    @GetMapping("/email-settings")
    public ApiResponse<EmailSettingsResponseDTO> getEmailSettings() {
        return ResponseWrapper.success(tonerOrderService.getEmailSettings());
    }
}
