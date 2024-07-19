package kr.or.kmi.mis.api.std.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import kr.or.kmi.mis.api.std.model.response.bcd.StdBcdResponseDTO;
import kr.or.kmi.mis.api.std.service.StdBcdService;
import kr.or.kmi.mis.cmm.response.ApiResponse;
import kr.or.kmi.mis.cmm.response.ResponseWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/std")
@RequiredArgsConstructor
@Tag(name = "StandardData(BCD)", description = "명함신청 기준자료 관련 API")
public class StdBcdController {

    private final StdBcdService stdBcdService;

    @GetMapping("/bcd")
    public ApiResponse<StdBcdResponseDTO> getBcdStd() {
        return ResponseWrapper.success(stdBcdService.getAllBcdStd());
    }

}
