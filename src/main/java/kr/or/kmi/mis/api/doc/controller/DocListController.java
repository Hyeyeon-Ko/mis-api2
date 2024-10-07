package kr.or.kmi.mis.api.doc.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.or.kmi.mis.api.doc.model.request.DocRequestDTO;
import kr.or.kmi.mis.api.doc.model.response.DocResponseDTO;
import kr.or.kmi.mis.api.doc.service.DocListService;
import kr.or.kmi.mis.api.docstorage.domain.response.DeptResponseDTO;
import kr.or.kmi.mis.cmm.model.request.PostPageRequest;
import kr.or.kmi.mis.cmm.model.request.PostSearchRequestDTO;
import kr.or.kmi.mis.cmm.model.response.ApiResponse;
import kr.or.kmi.mis.cmm.model.response.ResponseWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
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

    @Operation(summary = "get doc receive applyList", description = "문서 수신/발신 대장 조회")
    @GetMapping("/receiveList2")
    public ApiResponse<Page<DocResponseDTO>> getDocList(DocRequestDTO docRequestDTO,
                                                        PostSearchRequestDTO postSearchRequestDTO,
                                                        PostPageRequest pageRequest) {
        return ResponseWrapper.success(docListService.getDocList(docRequestDTO, postSearchRequestDTO, pageRequest.of()));
    }

    @Operation(summary = "get doc receive / send List", description = "문서발신대장 조회")
    @GetMapping("/sendList")
    public ApiResponse<List<DocResponseDTO>> getSendList(@RequestParam(required = false) LocalDateTime startDate,
                                                         @RequestParam(required = false) LocalDateTime endDate,
                                                         @RequestParam(required = false) String searchType,
                                                         @RequestParam(required = false) String keyword,
                                                         @RequestParam String instCd) {
        return ResponseWrapper.success(docListService.getSendApplyList(startDate, endDate, searchType, keyword, instCd));
    }


}
