package kr.or.kmi.mis.api.bcd.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.or.kmi.mis.api.bcd.model.request.BcdRequestDTO;
import kr.or.kmi.mis.api.bcd.model.response.BcdDetailResponseDTO;
import kr.or.kmi.mis.api.bcd.service.BcdService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/bcd")
@RequiredArgsConstructor
@Tag(name = "Bcd", description = "명함신청 관련 API")
public class BcdController {

    private final BcdService bcdService;

    @Operation(summary = "create bcd apply", description = "유저 > 명함신청 시 사용")
    @PostMapping(value = "/")
    public ResponseEntity<String> createBcdApply(@RequestBody BcdRequestDTO bcdRequestDTO) {

        bcdService.applyBcd(bcdRequestDTO);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body("명함이 신청되었습니다.");
    }

    @Operation(summary = "modify bcd apply", description = "유저 > 나의 신청내역 > 승인 대기 중인 명함신청 수정 시 사용")
    @PostMapping(value = "/update")
    public ResponseEntity<String> updateBcdApply(@RequestParam("draftId") Long draftId, @RequestParam("seqId") Long seqId, @RequestBody BcdRequestDTO bcdUpdateRequest) {
        bcdService.updateBcd(draftId, seqId, bcdUpdateRequest);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body("명함이 수정되었습니다.");
    }

    @Operation(summary = "cancel bcd apply", description = "유저 > 나의 신청내역 > 승인 대기 중인 명함신청 취소 시 사용")
    @PutMapping(value = "/{draftId}")
    public ResponseEntity<String> cancelBscApply(@PathVariable("draftId") Long draftId) {

        bcdService.cancelBcdApply(draftId);

        return ResponseEntity.status(HttpStatus.OK)
                .body("명함신청이 취소되었습니다.");
    }

    @Operation(summary = "get bcd detail", description = "유저 > 나의 신청내역 > 명함신청 상세 정보 조회 시 사용")
    @GetMapping(value = "/{draftId}")
    public BcdDetailResponseDTO getBcdDetail(@PathVariable("draftId") Long draftId) {

        return bcdService.getBcd(draftId);
    }

    @Operation(summary = "put status ORDERED into COMPLETED", description = "유저 > 나의 신청 내역 > 명함을 수령한 후, 발주 완료 버튼 클릭 시 사용")
    @PutMapping(value = "/completeApply")
    public ResponseEntity<String> completeBcdApply(@RequestParam("draftId") Long draftId) {

        bcdService.completeBcdApply(draftId);

        return ResponseEntity.status(HttpStatus.OK)
                .body("명함 수령이 완료되었습니다.");
    }

}
