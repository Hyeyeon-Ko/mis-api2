package kr.or.kmi.mis.api.doc.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.or.kmi.mis.api.doc.model.response.DocResponseDTO;
import kr.or.kmi.mis.api.doc.service.DocListService;
import kr.or.kmi.mis.api.docstorage.domain.response.DeptResponseDTO;
import kr.or.kmi.mis.cmm.model.response.ApiResponse;
import kr.or.kmi.mis.cmm.model.response.ResponseWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/doc")
@Tag(name = "Doc", description = "문서수발신 관련 API")
public class DocListController {

    public final DocListService docListService;

    @Operation(summary = "get doc receive applyList", description = "문서수신대장 조회")
    @GetMapping("/receiveList")
    public ApiResponse<List<DocResponseDTO>> getReceiveList(@RequestParam(required = false) LocalDateTime startDate,
                                                            @RequestParam(required = false) LocalDateTime endDate,
                                                            @RequestParam(required = false) String searchType,
                                                            @RequestParam(required = false) String keyword,
                                                            @RequestParam String instCd) {
        return ResponseWrapper.success(docListService.getReceiveApplyList(startDate, endDate, searchType, keyword, instCd));
    }

    @Operation(summary = "get doc receive applyList by deptCd", description = "부서별 문서수신대장 조회")
    @GetMapping("/deptReceiveList")
    public ApiResponse<List<DocResponseDTO>> getReceiveListByDeptCd(String deptCd) {
        return ResponseWrapper.success(docListService.getDeptReceiveApplyList(deptCd));
    }

    @Operation(summary = "get doc send applyList", description = "문서발신대장 조회")
    @GetMapping("/sendList")
    public ApiResponse<List<DocResponseDTO>> getSendList(@RequestParam(required = false) LocalDateTime startDate,
                                                         @RequestParam(required = false) LocalDateTime endDate,
                                                         @RequestParam(required = false) String searchType,
                                                         @RequestParam(required = false) String keyword,
                                                         @RequestParam String instCd) {
        return ResponseWrapper.success(docListService.getSendApplyList(startDate, endDate, searchType, keyword, instCd));
    }

    @Operation(summary = "get deptList", description = "문서수신대장 페이지 조회시 -> 부서 선택")
    @GetMapping("/deptList")
    public ApiResponse<List<DeptResponseDTO>> getDeptList(@RequestParam String instCd) {
        return ResponseWrapper.success(docListService.getDeptList(instCd));
    }
}
