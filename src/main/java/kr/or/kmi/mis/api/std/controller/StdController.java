package kr.or.kmi.mis.api.std.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.or.kmi.mis.api.std.model.request.StdDetailRequestDTO;
import kr.or.kmi.mis.api.std.model.request.StdDetailUpdateRequestDTO;
import kr.or.kmi.mis.api.std.model.request.StdGroupRequestDTO;
import kr.or.kmi.mis.api.std.model.response.StdDetailResponseDTO;
import kr.or.kmi.mis.api.std.model.response.StdGroupResponseDTO;
import kr.or.kmi.mis.api.std.service.StdDetailService;
import kr.or.kmi.mis.api.std.service.StdGroupService;
import kr.or.kmi.mis.cmm.response.ApiResponse;
import kr.or.kmi.mis.cmm.response.ResponseWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/std")
@RequiredArgsConstructor
@Tag(name = "StandardData", description = "기준자료 관련 API")
public class StdController {

    private final StdGroupService stdGroupService;
    private final StdDetailService stdDetailService;

    @Operation(summary = "get Group Info", description = "기준자료 > 중분류 정보 호출 시 사용")
    @GetMapping("/groupInfo")
    public ApiResponse<List<StdGroupResponseDTO>> getGroupInfo(@RequestParam("classCd") String classCd) {

        List<StdGroupResponseDTO> stdGroupResponses = stdGroupService.getInfo(classCd);

        return ResponseWrapper.success(stdGroupResponses);
    }

    @Operation(summary = "add Group Info", description = "기준자료 > 중분류 추가 시 사용")
    @PostMapping("/GroupInfo")
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

    @Operation(summary = "add Detail Info", description = "기준자료 > 상세 정보 추가 시 사용")
    @PostMapping("/detailInfo")
    public ApiResponse<?> addDetailInfo(@RequestBody StdDetailRequestDTO stdDetailRequestDTO) {

        stdDetailService.addInfo(stdDetailRequestDTO);

        return ResponseWrapper.success();
    }

    @Operation(summary = "update Detail Info", description = "기준자료 > 상세 정보 수정 시 사용")
    @PutMapping("/detailInfo")
    public ApiResponse<?> updateDetailInfo(@RequestBody StdDetailUpdateRequestDTO stdDetailRequestDTO) {

        stdDetailService.updateInfo(stdDetailRequestDTO);

        return ResponseWrapper.success();
    }

    @Operation(summary = "delete Detail Info", description = "기준자료 > 상세 정보 삭제 시 사용")
    @PutMapping("/deleteDetailInfo")
    public ApiResponse<?> deleteDetailInfo(@RequestParam("detailCd") String detailCd) {

        stdDetailService.deleteInfo(detailCd);

        return ResponseWrapper.success();
    }

}
