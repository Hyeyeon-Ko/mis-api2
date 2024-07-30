package kr.or.kmi.mis.api.std.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.or.kmi.mis.api.std.model.response.bcd.StdBcdResponseDTO;
import kr.or.kmi.mis.api.std.model.response.bcd.StdStatusResponseDTO;
import kr.or.kmi.mis.api.std.service.StdBcdService;
import kr.or.kmi.mis.cmm.model.response.ApiResponse;
import kr.or.kmi.mis.cmm.model.response.ResponseWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("api/std")
@RequiredArgsConstructor
@Tag(name = "StandardData(BCD)", description = "명함신청 기준자료 관련 API")
public class StdBcdController {

    private final StdBcdService stdBcdService;

    @Operation(summary = "get bcd standard data", description = "명함신청 시 필요한 모든 기준자료 데이터를 가져옵니다.")
    @GetMapping("/bcd")
    public ApiResponse<StdBcdResponseDTO> getBcdStd() {
        return ResponseWrapper.success(stdBcdService.getAllBcdStd());
    }

    @Operation(summary = "get bcd status", description = "명함신청상태 데이터를 가져옵니다.")
    @GetMapping("/status")
    public ApiResponse<List<StdStatusResponseDTO>> getStdStatus() {
        return ResponseWrapper.success(stdBcdService.getApplyStatus());
    }

}
