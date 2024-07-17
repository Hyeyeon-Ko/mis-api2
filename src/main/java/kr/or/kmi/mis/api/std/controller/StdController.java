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
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<List<StdGroupResponseDTO>> getGroupInfo(@RequestParam("classCd") String classCd) {

        List<StdGroupResponseDTO> stdGroupResponses = stdGroupService.getInfo(classCd);

        return ResponseEntity.status(HttpStatus.OK)
                .body(stdGroupResponses);
    }

    @Operation(summary = "add Group Info", description = "기준자료 > 중분류 추가 시 사용")
    @PostMapping("/GroupInfo")
    public ResponseEntity<String> addGroupInfo(@RequestBody StdGroupRequestDTO stdGroupRequestDTO) {

        stdGroupService.addInfo(stdGroupRequestDTO);

        return ResponseEntity.status(HttpStatus.OK)
                .body("중분류가 추가되었습니다.");
    }

    @Operation(summary = "get Detail Info", description = "기준자료 > 상세 정보 호출 시 사용")
    @GetMapping("/detailInfo")
    public ResponseEntity<List<StdDetailResponseDTO>> getDetailInfo(@RequestParam("groupCd") String groupCd) {

        List<StdDetailResponseDTO> stdDetailResponses = stdDetailService.getInfo(groupCd);

        return ResponseEntity.status(HttpStatus.OK)
                .body(stdDetailResponses);
    }

    @Operation(summary = "add Detail Info", description = "기준자료 > 상세 정보 추가 시 사용")
    @PostMapping("/detailInfo")
    public ResponseEntity<String> addDetailInfo(@RequestBody StdDetailRequestDTO stdDetailRequestDTO) {

        stdDetailService.addInfo(stdDetailRequestDTO);

        return ResponseEntity.status(HttpStatus.OK)
                .body("기준자료가 추가되었습니다.");
    }

    @Operation(summary = "update Detail Info", description = "기준자료 > 상세 정보 수정 시 사용")
    @PutMapping("/detailInfo")
    public ResponseEntity<String> updateDetailInfo(@RequestBody StdDetailUpdateRequestDTO stdDetailRequestDTO) {

        stdDetailService.updateInfo(stdDetailRequestDTO);

        return ResponseEntity.status(HttpStatus.OK)
                .body("기준자료가 수정되었습니다.");
    }

    @Operation(summary = "delete Detail Info", description = "기준자료 > 상세 정보 삭제 시 사용")
    @PutMapping("/deleteDetailInfo")
    public ResponseEntity<String> deleteDetailInfo(@RequestParam("detailCd") String detailCd) {

        stdDetailService.deleteInfo(detailCd);

        return ResponseEntity.status(HttpStatus.OK)
                .body("기준자료가 삭제되었습니다.");
    }

}
