package kr.or.kmi.mis.api.apply.controller;

import io.swagger.v3.oas.annotations.Operation;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import kr.or.kmi.mis.api.apply.model.request.ApplyRequestDTO;
import kr.or.kmi.mis.api.apply.model.response.ApplyResponseDTO;
import kr.or.kmi.mis.api.apply.model.response.MyApplyResponseDTO;
import kr.or.kmi.mis.api.apply.model.response.PendingCountResponseDTO;
import kr.or.kmi.mis.api.apply.model.response.PendingResponseDTO;
import kr.or.kmi.mis.api.apply.service.ApplyService;
import kr.or.kmi.mis.cmm.model.request.PostPageRequest;
import kr.or.kmi.mis.cmm.model.request.PostSearchRequestDTO;
import kr.or.kmi.mis.cmm.model.response.ApiResponse;
import kr.or.kmi.mis.cmm.model.response.ResponseWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Tag(name = "Apply", description = "신청목록 호출 관련 API")
public class ApplyController {

    private final ApplyService applyService;

    @Operation(summary = "신청 목록 호출", description = "총무팀 > 기준자료를 바탕으로, 전체 신청 목록 호출합니다.")
    @GetMapping(value = "/applyList")
    public ApiResponse<ApplyResponseDTO> getAllApplyList(@RequestParam String documentType,
                                                         @RequestParam(required = false) LocalDateTime startDate,
                                                         @RequestParam(required = false) LocalDateTime endDate,
                                                         @RequestParam(required = false) String searchType,
                                                         @RequestParam(required = false) String keyword,
                                                         @RequestParam String instCd,
                                                         @RequestParam String userId) {
        return ResponseWrapper.success(applyService.getAllApplyList(documentType, startDate, endDate, searchType, keyword, instCd, userId));
    }
    @Operation(summary = "신청 목록 호출", description = "총무팀 > 기준자료를 바탕으로, 전체 신청 목록 호출합니다.")
    @GetMapping(value = "/applyList2")
    public ApiResponse<ApplyResponseDTO> getAllApplyList2(@Valid ApplyRequestDTO applyRequestDTO,
                                                          @Valid PostSearchRequestDTO postSearchRequestDTO,
                                                          PostPageRequest page) {
        return ResponseWrapper.success(applyService.getAllApplyList2(applyRequestDTO, postSearchRequestDTO, page.of()));
    }

    @Operation(summary = "승인대기 신청목록 호출", description = "전체 신청목록들 가운데, 승인대기 상태인 목록만 호출합니다.")
    @GetMapping(value = "/pendingList")
    public ApiResponse<PendingResponseDTO> getPendingApplyList(@RequestParam String documentType,
                                                               @RequestParam(required = false) @DateTimeFormat(pattern = "yyyyMMdd") LocalDateTime startDate,
                                                               @RequestParam(required = false) @DateTimeFormat(pattern = "yyyyMMdd") LocalDateTime endDate,
                                                               @RequestParam String instCd,
                                                               @RequestParam String userId) {
        return ResponseWrapper.success(applyService.getPendingListByType(documentType, startDate, endDate, instCd, userId));
    }

    @Operation(summary = "승인대기 신청목록 호출", description = "전체 신청목록들 가운데, 승인대기 상태인 목록만 호출합니다.")
    @GetMapping(value = "/pendingList2")
    public ApiResponse<PendingResponseDTO> getPendingApplyList2(@Valid ApplyRequestDTO applyRequestDTO,
                                                                @Valid PostSearchRequestDTO postSearchRequestDTO,
                                                                PostPageRequest page) {
        return ResponseWrapper.success(applyService.getPendingListByType2(applyRequestDTO, postSearchRequestDTO, page.of()));
    }

    @Operation(summary = "승인대기내역 개수", description = "승인대기 내역의 개수를 알려줍니다.")
    @GetMapping(value = "/pendingCount")
    public ApiResponse<PendingCountResponseDTO> getPendingCountList(@Valid ApplyRequestDTO applyRequestDTO,
                                                                    @Valid PostSearchRequestDTO postSearchRequestDTO) {
        return ResponseWrapper.success(applyService.getPendingCountList(applyRequestDTO, postSearchRequestDTO));
    }

    @Operation(summary = "나의 신청내역 > 전체 신청목록 호출", description = "나의 모든 신청 내역을 호출합니다.")
    @GetMapping(value = "/myApplyList")
    public ApiResponse<MyApplyResponseDTO> getAllMyApplyList(@RequestParam String documentType,
                                                             @RequestParam(required = false) LocalDateTime startDate,
                                                             @RequestParam(required = false) LocalDateTime endDate,
                                                             @RequestParam String userId) {

        return ResponseWrapper.success(applyService.getAllMyApplyList(documentType, startDate, endDate, userId));
    }

    @Operation(summary = "나의 신청내역 > 전체 신청목록 호출", description = "나의 모든 신청 내역을 호출합니다.")
    @GetMapping(value = "/myApplyList2")
    public ApiResponse<MyApplyResponseDTO> getAllMyApplyList2(@Valid ApplyRequestDTO applyRequestDTO,
                                                              @Valid PostSearchRequestDTO postSearchRequestDTO,
                                                              PostPageRequest page) {

        return ResponseWrapper.success(applyService.getAllMyApplyList2(applyRequestDTO, postSearchRequestDTO, page.of()));
    }

    @Operation(summary = "나의 신청내역 > 승인대기 목록 호출", description = "나의 신청목록들 가운데, 승인대기 상태인 목록만 호출합니다.")
    @GetMapping(value = "/myPendingList")
    public ApiResponse<PendingResponseDTO> getMyPendingApplyList(String userId) {
        return ResponseWrapper.success(applyService.getMyPendingList(userId));
    }
}
