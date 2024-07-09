package kr.or.kmi.mis.api.bcd.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.or.kmi.mis.api.bcd.model.request.BcdRequestDTO;
import kr.or.kmi.mis.api.bcd.model.response.BcdMasterResponseDTO;
import kr.or.kmi.mis.api.bcd.model.response.BcdPendingResponseDTO;
import kr.or.kmi.mis.api.bcd.service.BcdService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    @PostMapping(value = "/{draftId}")
    public ResponseEntity<String> updateBcdApply(@PathVariable("draftId") Long draftId, @RequestBody BcdRequestDTO bcdUpdateRequest) {
        bcdService.updateBcd(draftId, bcdUpdateRequest);

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

/*    // todo: 미정... 기준자료? applyController 랑 중복되면 삭제!
    @Operation(summary = "get all bcd apply", description = "관리자 > 전체 신청 목록 > ")
    @GetMapping(value = "/applyList")
    public ResponseEntity<List<BcdMasterResponseDTO>> getAllBcdApplyList() {

        List<BcdMasterResponseDTO> bcdMasterResponseDTOS = bcdService.getAllBcdApply();

        return ResponseEntity.status(HttpStatus.OK)
                .body(bcdMasterResponseDTOS);
    }*/

    // todo: 기안일자 포함해서 필터링 후, return? 아니면 그냥 이대로하고 기안일자는 프론트단에서 처리?
    @Operation(summary = "get all my bcd apply", description = "유저 > 나의 신청 내역 > 모든 명함 신청내역 조회")
    @GetMapping(value = "/myApplyList")
    public ResponseEntity<List<BcdMasterResponseDTO>> getAllMyBcdApplyList() {

        List<BcdMasterResponseDTO> bcdMasterResponseDTOS = bcdService.getMyBcdApply();

        return ResponseEntity.status(HttpStatus.OK)
                .body(bcdMasterResponseDTOS);
    }

    // todo: 명함신청 뿐만 아니라 다른 신청내역 모두 포함하여 떠야함
    @Operation(summary = "get all my pending bcd apply list", description = "유저 > 나의 신청 내역 > 모든? 승인 대기 목록 조회")
    @GetMapping(value = "/myPendingList")
    public ResponseEntity<List<BcdPendingResponseDTO>> getMyPendingBcdApplyList() {

        List<BcdPendingResponseDTO> bcdPendingResponseDTOS = bcdService.getMyPendingList();

        return ResponseEntity.status(HttpStatus.OK)
                .body(bcdPendingResponseDTOS);
    }

    @Operation(summary = "put status ORDERED into COMPLETED", description = "유저 > 나의 신청 내역 > 명함을 수령한 후, 발주 완료 버튼 클릭 시 사용")
    @PutMapping(value = "/completeApply")
    public ResponseEntity<String> completeBcdApply(@RequestParam("draftId") Long draftId) {

        bcdService.completeBcdApply(draftId);

        return ResponseEntity.status(HttpStatus.OK)
                .body("명함 수령이 완료되었습니다.");
    }

}
