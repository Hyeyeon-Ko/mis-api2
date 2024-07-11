package kr.or.kmi.mis.api.main.controller;

import io.swagger.v3.oas.annotations.Operation;

import kr.or.kmi.mis.api.main.model.response.ApplyResponseDTO;
import kr.or.kmi.mis.api.main.model.response.PendingResponseDTO;
import kr.or.kmi.mis.api.main.service.ApplyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ApplyController {

    private final ApplyService applyService;

    // todo: 기준자료(권한)에서 접근 가능한 기준자료 확인
    @Operation(summary = "전체 신청 목록 호출", description = "총무팀 > 기준자료를 바탕으로, 전체 신청 목록 호출합니다.")
    @GetMapping(value = "/applyList")
    public ResponseEntity<ApplyResponseDTO> getAllApplyList(@RequestParam(required = false) String documentType,
                                                            @RequestParam(required = false) LocalDate startDate,
                                                            @RequestParam(required = false) LocalDate endDate) {

        ApplyResponseDTO allApplyLists = applyService.getAllApplyList(documentType, startDate, endDate);
        return ResponseEntity.status(HttpStatus.OK).
                body(allApplyLists);
    }

    @Operation(summary = "전체 승인대기 신청목록 호출", description = "전체 신청목록들 가운데, 승인대기 상태인 목록만 호출합니다.")
    @GetMapping(value = "/pendingList")
    public ResponseEntity<PendingResponseDTO> getPendingApplyList() {

        return ResponseEntity.status(HttpStatus.OK).
                body(applyService.getAllPendingList());
    }


    @Operation(summary = "나의 신청 내역 호출", description = "나의 모든 신청 내역을 호출합니다.")
    @GetMapping(value = "/myApplyList")
    public ResponseEntity<ApplyResponseDTO> getAllMyApplyList(@RequestParam(required = false) String documentType,
                                                              @RequestParam(required = false) LocalDate startDate,
                                                              @RequestParam(required = false) LocalDate endDate) {

        ApplyResponseDTO myApplyLists = applyService.getAllMyApplyList(documentType, startDate, endDate);
        return ResponseEntity.status(HttpStatus.OK)
                .body(myApplyLists);
    }

    @Operation(summary = "나의 승인대기 신청목록 호출", description = "나의 신청목록들 가운데, 승인대기 상태인 목록만 호출합니다.")
    @GetMapping(value = "/myPendingList")
    public ResponseEntity<PendingResponseDTO> getMyPendingApplyList() {

        PendingResponseDTO myPendingList = applyService.getMyPendingList();

        return ResponseEntity.status(HttpStatus.OK)
                .body(myPendingList);
    }
}
