package kr.or.kmi.mis.api.main.controller;

import io.swagger.v3.oas.annotations.Operation;

import kr.or.kmi.mis.api.main.model.response.ApplyResponseDTO;
import kr.or.kmi.mis.api.main.service.ApplyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

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

        if (startDate == null) {
            startDate = LocalDate.now().minusMonths(1);
        }
        if (endDate == null) {
            endDate = LocalDate.now();
        }

        Timestamp startTimestamp = Timestamp.valueOf(startDate.atStartOfDay());
        Timestamp endTimestamp = Timestamp.valueOf(endDate.atTime(LocalTime.MAX));

        ApplyResponseDTO allApplyLists = applyService.getAllApplyList(documentType, startTimestamp, endTimestamp);
        return ResponseEntity.status(HttpStatus.OK).
                body(allApplyLists);
    }

    @Operation(summary = "나의 신청 내역 호출", description = "나의 모든 신청 내역을 호출합니다.")
    @GetMapping(value = "/myApplyList")
    public ResponseEntity<ApplyResponseDTO> getAllMyApplyList(@RequestParam(required = false) String documentType,
                                                              @RequestParam(required = false) LocalDate startDate,
                                                              @RequestParam(required = false) LocalDate endDate) {

        if (startDate == null) {
            startDate = LocalDate.now().minusMonths(1);
        }
        if (endDate == null) {
            endDate = LocalDate.now();
        }

        Timestamp startTimestamp = Timestamp.valueOf(startDate.atStartOfDay());
        Timestamp endTimestamp = Timestamp.valueOf(endDate.atTime(LocalTime.MAX));

        ApplyResponseDTO myApplyLists = applyService.getAllMyApplyList(documentType, startTimestamp, endTimestamp);
        return ResponseEntity.status(HttpStatus.OK)
                .body(myApplyLists);
    }
}
