package kr.or.kmi.mis.api.bcd.controller;

import kr.or.kmi.mis.api.bcd.model.request.BcdRequest;
import kr.or.kmi.mis.api.bcd.model.response.BcdMasterResponse;
import kr.or.kmi.mis.api.bcd.service.BcdService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/bcd")
@RequiredArgsConstructor
public class BcdController {

    private final BcdService bcdService;

    // 개인 페이지 > 명함신청
    @PostMapping(value = "/")
    @ResponseStatus(value = HttpStatus.CREATED)
    public ResponseEntity<String> createBscApply(@RequestBody BcdRequest bcdRequest) {

        bcdService.applyBcd(bcdRequest);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body("명함이 신청되었습니다.");
    }

    // 개인 페이지 > 명함신청 정보 수정
    @PostMapping(value = "/{draftId}")
    public ResponseEntity<String> updateBcdApply(@PathVariable("draftId") Long draftId, @RequestBody BcdRequest bcdUpdateRequest) {
        bcdService.updateBcd(draftId, bcdUpdateRequest);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body("명함이 수정되었습니다.");
    }

    // 개인 페이지 > 명함신청 취소
    @PutMapping(value = "/{draftId}")
    public ResponseEntity<String> cancelBscApply(@PathVariable("draftId") Long draftId) {

        bcdService.cancelBcdApply(draftId);

        return ResponseEntity.status(HttpStatus.OK)
                .body("명함신청이 취소되었습니다.");
    }

    // 관리자 페이지 > 전체 명함신청목록 조회
    @GetMapping(value = "/applyList")
    public ResponseEntity<List<BcdMasterResponse>> getAllBcdApplyList() {

        List<BcdMasterResponse> bcdMasterResponses = bcdService.getBcdApply();

        return ResponseEntity.status(HttpStatus.OK)
                .body(bcdMasterResponses);
    }

    // 개인 페이지 > 나의 명함신청내역 조회
    @GetMapping(value = "/myApplyList")
    public ResponseEntity<List<BcdMasterResponse>> getAllMyBcdApplyList() {

        List<BcdMasterResponse> bcdMasterResponses = bcdService.getMyBcdApply();

        return ResponseEntity.status(HttpStatus.OK)
                .body(bcdMasterResponses);
    }

/*    // 개인 페이지 > 승인대기 목록 조회
    @GetMapping(value = "/myPendingList")
    public ResponseEntity<List<BcdPendingResponse>> getMyPendingBcdApplyList() {

        List<BcdPendingResponse> bcdPendingResponses = bcdService.getMyPendingList();

        return ResponseEntity.status(HttpStatus.OK)
                .body(bcdPendingResponses);
    }*/

}
