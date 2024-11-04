package kr.or.kmi.mis.api.toner.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.or.kmi.mis.api.toner.model.response.TonerPendingResponseDTO;
import kr.or.kmi.mis.api.toner.service.TonerPendingService;
import kr.or.kmi.mis.cmm.model.response.ApiResponse;
import kr.or.kmi.mis.cmm.model.response.ResponseWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/toner/pending")
@Tag(name = "TonerPendingList", description = "토너 대기내역 관련 API")
public class TonerPendingController {

    private final TonerPendingService tonerPendingService;

    @Operation(summary = "get Toner Pending List", description = "센터별 토너 대기 목록 조회")
    @GetMapping
    public ApiResponse<List<TonerPendingResponseDTO>> getTonerPendingList(String instCd){
        return ResponseWrapper.success(tonerPendingService.getTonerPendingList(instCd));
    }
}
