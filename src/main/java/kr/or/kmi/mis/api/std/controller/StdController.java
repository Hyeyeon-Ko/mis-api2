package kr.or.kmi.mis.api.std.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.or.kmi.mis.api.std.model.request.StdDetailRequestDTO;
import kr.or.kmi.mis.api.std.model.request.StdDetailUpdateRequestDTO;
import kr.or.kmi.mis.api.std.model.response.StdDetailResponseDTO;
import kr.or.kmi.mis.api.std.service.StdDetailService;
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

    private final StdDetailService stdDetailService;

    @Operation(summary = "get center Info", description = "기준자료 > 중분류 정보 호출 시 사용")
    @GetMapping("/info")
    public ResponseEntity<List<StdDetailResponseDTO>> getCenterInfo(@RequestParam("groupCd") String groupCd) {

        List<StdDetailResponseDTO> stdDetailResponses = stdDetailService.getInfo(groupCd);

        return ResponseEntity.status(HttpStatus.OK)
                .body(stdDetailResponses);
    }

    @Operation(summary = "add center Info", description = "기준자료 > 소분류 정보 추가 시 사용")
    @PostMapping("/info")
    public ResponseEntity<String> addCenterInfo(@RequestBody StdDetailRequestDTO stdDetailRequestDTO) {

        stdDetailService.addInfo(stdDetailRequestDTO);

        return ResponseEntity.status(HttpStatus.OK)
                .body("센터정보가 추가되었습니다.");
    }

    @Operation(summary = "update center Info", description = "기준자료 > 소분류 정보 수정 시 사용")
    @PutMapping("/info")
    public ResponseEntity<String> updateCenterInfo(@RequestBody StdDetailUpdateRequestDTO stdDetailRequestDTO) {

        stdDetailService.updateInfo(stdDetailRequestDTO);

        return ResponseEntity.status(HttpStatus.OK)
                .body("센터정보가 수정되었습니다.");
    }

    @Operation(summary = "delete center Info", description = "기준자료 > 소분류 정보 삭제 시 사용")
    @PutMapping("/deleteInfo")
    public ResponseEntity<String> deleteCenterInfo(@RequestParam("detailCd") String detailCd) {

        stdDetailService.deleteInfo(detailCd);

        return ResponseEntity.status(HttpStatus.OK)
                .body("센터정보가 삭제되었습니다.");
    }

}
