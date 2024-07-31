package kr.or.kmi.mis.api.apply.controller;

import io.swagger.v3.oas.annotations.Operation;

import io.swagger.v3.oas.annotations.tags.Tag;
import kr.or.kmi.mis.api.apply.model.response.ApplyResponseDTO;
import kr.or.kmi.mis.api.apply.model.response.MyApplyResponseDTO;
import kr.or.kmi.mis.api.apply.model.response.PendingResponseDTO;
import kr.or.kmi.mis.api.apply.service.ApplyService;
import kr.or.kmi.mis.cmm.model.response.ApiResponse;
import kr.or.kmi.mis.cmm.model.response.ResponseWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Tag(name = "Apply", description = "신청목록 호출 관련 API")
public class ApplyController {

    private final ApplyService applyService;

    @Operation(summary = "전체 신청 목록 호출", description = "총무팀 > 기준자료를 바탕으로, 전체 신청 목록 호출합니다.")
    @GetMapping(value = "/applyList")
    public ApiResponse<ApplyResponseDTO> getAllApplyList(@RequestParam(required = false) String documentType,
                                                         @RequestParam(required = false) LocalDate startDate,
                                                         @RequestParam(required = false) LocalDate endDate) {

        return ResponseWrapper.success(applyService.getAllApplyList(documentType, startDate, endDate));
    }

//    @Operation(summary = "전체 승인대기 신청목록 호출", description = "전체 신청목록들 가운데, 승인대기 상태인 목록만 호출합니다.")
//    @GetMapping(value = "/pendingList")
//    public ApiResponse<PendingResponseDTO> getPendingApplyList() {
//
//        return ResponseWrapper.success(applyService.getAllPendingList());
//    }


    @Operation(summary = "나의 신청내역 > 전체 신청목록 호출", description = "나의 모든 신청 내역을 호출합니다.")
    @GetMapping(value = "/myApplyList")
    public ApiResponse<MyApplyResponseDTO> getAllMyApplyList(@RequestParam(required = false) String documentType,
                                                             @RequestParam(required = false) LocalDate startDate,
                                                             @RequestParam(required = false) LocalDate endDate) {

        return ResponseWrapper.success(applyService.getAllMyApplyList(documentType, startDate, endDate));
    }

    @Operation(summary = "나의 신청내역 > 승인대기 목록 호출", description = "나의 신청목록들 가운데, 승인대기 상태인 목록만 호출합니다.")
    @GetMapping(value = "/myPendingList")
    public ApiResponse<PendingResponseDTO> getMyPendingApplyList() {

        return ResponseWrapper.success(applyService.getMyPendingList());
    }
}
