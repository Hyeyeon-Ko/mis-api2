package kr.or.kmi.mis.api.toner.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.or.kmi.mis.api.toner.model.request.TonerApplyRequestDTO;
import kr.or.kmi.mis.api.toner.model.response.TonerApplyResponseDTO;
import kr.or.kmi.mis.api.toner.model.response.TonerInfo2ResponseDTO;
import kr.or.kmi.mis.api.toner.service.TonerService;
import kr.or.kmi.mis.cmm.model.response.ApiResponse;
import kr.or.kmi.mis.cmm.model.response.ResponseWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/toner")
@RequiredArgsConstructor
@Tag(name = "TonerList", description = "토너 신청 관련 API")
public class TonerController {

    private final TonerService tonerService;

    @Operation(summary = "토너 상세정보를 조회합니다.")
    @GetMapping("/info")
    public ApiResponse<TonerInfo2ResponseDTO> getTonerInfo(@RequestParam("mngNum") String mngNum ) {

        return ResponseWrapper.success(tonerService.getTonerInfo(mngNum));
    }

    @Operation(summary = "토너신청 상세정보를 조회합니다.")
    @GetMapping("/apply")
    public ApiResponse<TonerApplyResponseDTO> getTonerApply(@RequestParam("draftId") Long draftId ) {

        return ResponseWrapper.success(tonerService.getTonerApply(draftId));
    }

    @Operation(summary = "토너를 신청합니다.")
    @PostMapping("")
    public ApiResponse<?> applyToner(@RequestBody TonerApplyRequestDTO tonerRequestDTO ) {
        tonerService.applytoner(tonerRequestDTO);

        return ResponseWrapper.success();
    }

//    @Operation(summary = "토너신청을 수정합니다.")
//    @PutMapping("")
//    public ApiResponse<?> updateTonerApply(@RequestBody TonerRequestDTO tonerRequestDTO ) {
//        tonerService.updateTonerApply(tonerRequestDTO);
//
//        return ResponseWrapper.success();
//    }

    @Operation(summary = "토너신청을 취소합니다.")
    @PutMapping("/cancel")
    public ApiResponse<?> cancelTonerApply(@RequestParam("draftId") Long draftId ) {
        tonerService.cancelTonerApply(draftId);

        return ResponseWrapper.success();
    }

}
