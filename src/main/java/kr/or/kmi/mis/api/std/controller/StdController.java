package kr.or.kmi.mis.api.std.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.or.kmi.mis.api.std.model.request.StdClassRequestDTO;
import kr.or.kmi.mis.api.std.model.request.StdDetailRequestDTO;
import kr.or.kmi.mis.api.std.model.request.StdDetailUpdateRequestDTO;
import kr.or.kmi.mis.api.std.model.request.StdGroupRequestDTO;
import kr.or.kmi.mis.api.std.model.response.StdClassResponseDTO;
import kr.or.kmi.mis.api.std.model.response.StdDetailResponseDTO;
import kr.or.kmi.mis.api.std.model.response.StdGroupResponseDTO;
import kr.or.kmi.mis.api.std.model.response.StdResponseDTO;
import kr.or.kmi.mis.api.std.service.StdClassService;
import kr.or.kmi.mis.api.std.service.StdDetailService;
import kr.or.kmi.mis.api.std.service.StdGroupService;
import kr.or.kmi.mis.cmm.model.request.PostPageRequest;
import kr.or.kmi.mis.cmm.model.response.ApiResponse;
import kr.or.kmi.mis.cmm.model.response.ResponseWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/std")
@RequiredArgsConstructor
@Tag(name = "StandardData", description = "기준자료 관련 API")
public class StdController {

    private final StdClassService stdClassService;
    private final StdGroupService stdGroupService;
    private final StdDetailService stdDetailService;

    @Operation(summary = "get Std Header Name", description = "각 기준자료의 헤더명 호출 시 사용")
    @GetMapping("/header")
    public ApiResponse<List<StdDetailResponseDTO>> getHeaderInfo(String groupCd) {
        return ResponseWrapper.success(stdDetailService.getHeaderInfo(groupCd));
    }

    @Operation(summary = "get Class Info", description = "기준자료 > 대분류 정보 호출 시 사용")
    @GetMapping("/classInfo")
    public ApiResponse<List<StdClassResponseDTO>> getClassInfo() {

        List<StdClassResponseDTO> stdClassResponses = stdClassService.getInfo();

        return ResponseWrapper.success(stdClassResponses);
    }

    @Operation(summary = "add Class Info", description = "기준자료 > 대분류 정보 추가 시 사용")
    @PostMapping("/classInfo")
    public ApiResponse<?> addClassInfo(@RequestBody StdClassRequestDTO stdClassRequestDTO) {

        stdClassService.addClassInfo(stdClassRequestDTO);

        return ResponseWrapper.success();
    }

    @Operation(summary = "get Group Info", description = "기준자료 > 중분류 정보 호출 시 사용")
    @GetMapping("/groupInfo")
    public ApiResponse<List<StdGroupResponseDTO>> getGroupInfo(@RequestParam("classCd") String classCd) {

        List<StdGroupResponseDTO> stdGroupResponses = stdGroupService.getInfo(classCd);

        return ResponseWrapper.success(stdGroupResponses);
    }

    @Operation(summary = "add Group Info", description = "기준자료 > 중분류 추가 시 사용")
    @PostMapping("/groupInfo")
    public ApiResponse<?> addGroupInfo(@RequestBody StdGroupRequestDTO stdGroupRequestDTO) {

        stdGroupService.addInfo(stdGroupRequestDTO);

        return ResponseWrapper.success();
    }

    @Operation(summary = "get Detail Info", description = "기준자료 > 상세 정보 호출 시 사용")
    @GetMapping("/detailInfo")
    public ApiResponse<List<StdDetailResponseDTO>> getDetailInfo(@RequestParam("groupCd") String groupCd) {

        List<StdDetailResponseDTO> stdDetailResponses = stdDetailService.getInfo(groupCd);

        return ResponseWrapper.success(stdDetailResponses);
    }

    @Operation(summary = "get Detail Info", description = "기준자료 > 상세 정보 호출 시 사용 및 페이징 처리")
    @GetMapping("/detailInfo2")
    public ApiResponse<Page<StdDetailResponseDTO>> getDetailInfo(@RequestParam("groupCd") String groupCd, PostPageRequest page) {
        return ResponseWrapper.success(stdDetailService.getInfo2(groupCd, page.of()));
    }

    @Operation(summary = "add Detail Info", description = "기준자료 > 상세 정보 추가 시 사용")
    @PostMapping("/detailInfo")
    public ApiResponse<?> addDetailInfo(@RequestBody StdDetailRequestDTO stdDetailRequestDTO) {

        stdDetailService.addInfo(stdDetailRequestDTO);

        return ResponseWrapper.success();
    }

    @Operation(summary = "update Detail Info", description = "기준자료 > 상세 정보 수정 시 사용")
    @PutMapping("/detailInfo")
    public ApiResponse<?> updateDetailInfo(@RequestBody StdDetailUpdateRequestDTO stdDetailRequestDTO,
                                           @RequestParam("oriDetailCd") String oriDetailCd) {

        stdDetailService.updateInfo(stdDetailRequestDTO, oriDetailCd);

        return ResponseWrapper.success();
    }

    @Operation(summary = "delete Detail Info", description = "기준자료 > 상세 정보 삭제 시 사용")
    @PutMapping("/deleteDetailInfo")
    public ApiResponse<?> deleteDetailInfo(@RequestParam("groupCd") String groupCd, @RequestParam("detailCd") String detailCd) {

        stdDetailService.deleteInfo(groupCd, detailCd);

        return ResponseWrapper.success();
    }

    @Operation(summary = "get Selected Detail Info", description = "기준자료 > 상세정보 수정 시 사용")
    @GetMapping("/detailInfo/{detailCd}")
    public ApiResponse<StdDetailResponseDTO> getSelectedDetailInfo(@RequestParam("groupCd") String groupCd, @PathVariable String detailCd) {

        return ResponseWrapper.success(stdDetailService.getSelectedInfo(groupCd, detailCd));
    }

    @Operation(summary = "조직도 정보 조회", description = "계층형 조직도 정보 조회")
    @GetMapping("/orgChart")
    public ApiResponse<List<StdResponseDTO>> getOrgChart(String instCd, String deptCode) {
        List<StdResponseDTO> orgChart = stdDetailService.getOrgChart(instCd, deptCode);
        return ResponseWrapper.success(orgChart);
    }
}
