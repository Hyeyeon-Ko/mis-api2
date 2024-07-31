package kr.or.kmi.mis.api.doc.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.or.kmi.mis.api.doc.model.response.docResponseDTO;
import kr.or.kmi.mis.api.doc.service.DocSRService;
import kr.or.kmi.mis.cmm.model.response.ApiResponse;
import kr.or.kmi.mis.cmm.model.response.ResponseWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/doc")
@Tag(name = "Doc", description = "문서수발신 관련 API")
public class DocSRController {

    public final DocSRService docSRService;

    @Operation(summary = "get doc receive applyList", description = "문서수신대장 조회")
    @PutMapping("/receiveList")
    public ApiResponse<List<docResponseDTO>> getReceiveList() {
        List<docResponseDTO> receiveApplyList = docSRService.getReceiveApplyList();
        return ResponseWrapper.success(receiveApplyList);
    }

    @Operation(summary = "get doc send applyList", description = "문서발신대장 조회")
    @PutMapping("/sendList")
    public ApiResponse<List<docResponseDTO>> getSendList() {
        List<docResponseDTO> sendApplyList = docSRService.getSendApplyList();
        return ResponseWrapper.success(sendApplyList);
    }
}
