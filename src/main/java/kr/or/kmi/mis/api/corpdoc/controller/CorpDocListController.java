package kr.or.kmi.mis.api.corpdoc.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.or.kmi.mis.api.corpdoc.model.request.CorpDocLeftRequestDTO;
import kr.or.kmi.mis.api.corpdoc.model.request.CorpDocStoreRequestDTO;
import kr.or.kmi.mis.api.corpdoc.model.response.CorpDocIssueListResponseDTO;
import kr.or.kmi.mis.api.corpdoc.model.response.CorpDocRnpResponseDTO;
import kr.or.kmi.mis.api.corpdoc.service.CorpDocListService;
import kr.or.kmi.mis.cmm.model.request.PostPageRequest;
import kr.or.kmi.mis.cmm.model.request.PostSearchRequestDTO;
import kr.or.kmi.mis.cmm.model.response.ApiResponse;
import kr.or.kmi.mis.cmm.model.response.ResponseWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/corpDoc")
@RequiredArgsConstructor
@Tag(name="CorpDocList", description = "법인서류 관련 리스트 조회 API")
public class CorpDocListController {

    public final CorpDocListService corpDocListService;

    @Operation(summary = "get Issuance List of corpDoc", description = "법인서류 발급대장 리스트 조회")
    @GetMapping(value = "/issueList")
    public ApiResponse<CorpDocIssueListResponseDTO> getCorpDocIssueList(PostSearchRequestDTO postSearchRequestDTO, PostPageRequest page) {
        return ResponseWrapper.success(corpDocListService.getCorpDocIssueList(postSearchRequestDTO, page.of()));
    }

    @Operation(summary = "get Receive and Payment List of corpDoc", description = "법인서류 수불대장 리스트 조회")
        @GetMapping(value = "/rnpList")
        public ApiResponse<Page<CorpDocRnpResponseDTO>> getCorpDocRnpList(String instCd, PostSearchRequestDTO postSearchRequestDTO, PostPageRequest page) {
            return ResponseWrapper.success(corpDocListService.getCorpDocRnpList(instCd, postSearchRequestDTO, page.of()));
        }

    @Operation(summary = "issue corpDoc", description = "법인서류 발급")
    @PutMapping(value = "/issue")
    public ApiResponse<?> issueCorpDoc(@RequestParam("draftId") String draftId, @RequestBody CorpDocLeftRequestDTO requestDTO) {
        corpDocListService.issueCorpDoc(draftId, requestDTO);
        return ResponseWrapper.success();
    }

    @Operation(summary = "store corpDoc", description = "법인서류 입고 등록")
    @PostMapping(value = "/store")
    public ApiResponse<?> storeCorpDoc(@RequestBody CorpDocStoreRequestDTO corpDocStoreRequestDTO) {
        corpDocListService.storeCorpDoc(corpDocStoreRequestDTO);
        return ResponseWrapper.success();
    }

    @Operation(summary = "complete corpDoc apply", description = "법인서류 수령 확인")
    @PutMapping(value = "/completeApply")
    public ApiResponse<?> completeCorpDoc(@RequestParam("draftId") String draftId) {
        corpDocListService.completeCorpDoc(draftId);
        return ResponseWrapper.success();
    }
}
