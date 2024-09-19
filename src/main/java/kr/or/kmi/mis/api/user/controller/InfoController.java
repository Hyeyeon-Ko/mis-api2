package kr.or.kmi.mis.api.user.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.or.kmi.mis.api.user.model.response.ConfirmResponseDTO;
import kr.or.kmi.mis.api.user.model.response.InfoDetailResponseDTO;
import kr.or.kmi.mis.api.user.model.response.InfoResponseDTO;
import kr.or.kmi.mis.api.user.model.response.OrgChartResponseDTO;
import kr.or.kmi.mis.api.user.service.InfoService;
import kr.or.kmi.mis.cmm.model.response.ApiResponse;
import kr.or.kmi.mis.cmm.model.response.ResponseWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/info")
@Tag(name = "Info", description = "그룹웨어 관련 API")
public class InfoController {

    private final InfoService infoService;

    @Operation(summary = "로그인 사용자 정보 가져오기", description = "세션에서 현재 로그인 된 사용자 정보(사번, 이름)을 가져옵니다.")
    @GetMapping(value = "/")
    public ApiResponse<InfoResponseDTO> getUserInfo() {
        return ResponseWrapper.success(infoService.getUserInfo());
    }

    @Operation(summary = "그룹웨어 정보 가져오기", description = "사번으로 그룹웨어에서 사용자 정보를 가져옵니다. 명함 신청 시 사용할 수 있습니다.")
    @GetMapping(value = "/{userId}")
    public ApiResponse<InfoDetailResponseDTO> getUserInfoDetail(@PathVariable String userId) {
        return ResponseWrapper.success(infoService.getUserInfoDetail(userId));
    }

    @Operation(summary = "조직도 정보 가져오기", description = "전체 조직도 정보를 가져옵니다.")
    @GetMapping(value = "/orgChart")
    public ApiResponse<List<OrgChartResponseDTO>> getOrgChartInfo(@RequestParam String detailCd) {
        return ResponseWrapper.success(infoService.getOrgChartInfo(detailCd));
    }

    @Operation(summary = "로그인한 사용자 센터의 경영지원팀 승인라인 가져오기", description = "승인라인을 가져옵니다.")
    @GetMapping(value = "/confirm")
    public ApiResponse<List<ConfirmResponseDTO>> getConfirmInfo(@RequestParam String instCd) {
        return ResponseWrapper.success(infoService.getConfirmInfo(instCd));
    }
}
