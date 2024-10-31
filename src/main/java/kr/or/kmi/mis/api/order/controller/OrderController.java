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
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.util.List;
import java.util.Objects;

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
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<?> orderRequest(
            @RequestPart("orderRequest") OrderRequestDTO orderRequest,
            @RequestPart(value = "previewFile", required = false) MultipartFile previewFile,
            @RequestPart(value = "files", required = false) List<MultipartFile> files
    ) throws IOException, MessagingException, GeneralSecurityException {

        if (previewFile != null && !Objects.equals(previewFile.getContentType(), "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")) {
            throw new IllegalArgumentException("미리보기 파일은 엑셀(.xlsx) 형식이어야 합니다.");
        }

        orderService.orderRequest(orderRequest, previewFile, files);
        return ResponseWrapper.success();
    }

    @Operation(summary = "preview order file", description = "발주 미리보기 파일 다운로드")
    @GetMapping("/preview")
    public ResponseEntity<byte[]> previewOrderFile(@RequestParam List<String> draftIds) throws IOException {
        byte[] fileData = orderService.previewOrderFile(draftIds);

        String encodedFileName = URLEncoder.encode("명함발주.xlsx", StandardCharsets.UTF_8).replaceAll("\\+", "%20");

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename*=UTF-8''" + encodedFileName)
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(fileData);
    }

    @Operation(summary = "get email settings", description = "기본 이메일 설정 조회")
    @GetMapping("/email-settings")
    public ApiResponse<EmailSettingsResponseDTO> getEmailSettings() {
        return ResponseWrapper.success(orderService.getEmailSettings());
    }
}
