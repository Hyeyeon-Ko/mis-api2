package kr.or.kmi.mis.api.doc.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.or.kmi.mis.api.doc.model.response.DocResponseDTO;
import kr.or.kmi.mis.api.doc.service.DocListService;
import kr.or.kmi.mis.cmm.model.response.ApiResponse;
import kr.or.kmi.mis.cmm.model.response.ResponseWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/doc")
@Tag(name = "Doc", description = "문서수발신 관련 API")
public class DocListController {

    public final DocListService docListService;

    @Operation(summary = "get doc receive applyList", description = "문서수신대장 조회")
    @GetMapping("/receiveList")
    public ApiResponse<List<DocResponseDTO>> getReceiveList(@RequestParam(required = false) LocalDate startDate,
                                                            @RequestParam(required = false) LocalDate endDate) {
        return ResponseWrapper.success(docListService.getReceiveApplyList(startDate, endDate));
    }

    @Operation(summary = "get doc send applyList", description = "문서발신대장 조회")
    @GetMapping("/sendList")
    public ApiResponse<List<DocResponseDTO>> getSendList(@RequestParam(required = false) LocalDate startDate,
                                                         @RequestParam(required = false) LocalDate endDate) {
        return ResponseWrapper.success(docListService.getSendApplyList(startDate, endDate));
    }
}
