package kr.or.kmi.mis.api.confirm.controller;

import kr.or.kmi.mis.api.bcd.model.response.BcdDetailResponseDTO;
import kr.or.kmi.mis.api.confirm.service.ConfirmService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/confirm")
public class ConfirmController {

    private ConfirmService confirmService;

    /* 신청 목록 상세 조회 */
    @GetMapping("applyList/{draftId}")
    public ResponseEntity<BcdDetailResponseDTO> getApplyList(@RequestParam("draftId") Long id) {
        BcdDetailResponseDTO bcdDetail = confirmService.getBcdDetailInfo(id);
        return ResponseEntity.status(HttpStatus.OK).body(bcdDetail);
    }

//    /* 승인 */
//    @PostMapping("applyList/{draftId}}")
//    public ResponseEntity<String> approve(@RequestParam("draftId") Long id) {
//        confirmService.approve(id);
//        return new ResponseEntity<>("ok", HttpStatus.OK);
//    }
//
//    /* 반려 */
//    @PostMapping("applyList/return/{draftId}")
//    public ResponseEntity<String> reject(@RequestParam("draftId") Long id, @RequestBody String rejectReason) {
//        confirmService.reject(id, rejectReason);
//        return new ResponseEntity<>("ok", HttpStatus.OK);
//    }
}
