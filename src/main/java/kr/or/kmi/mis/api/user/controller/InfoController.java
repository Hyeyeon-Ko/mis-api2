package kr.or.kmi.mis.api.user.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import kr.or.kmi.mis.api.user.model.response.InfoResponseDTO;
import kr.or.kmi.mis.api.user.service.InfoService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/info")
@Tag(name = "Info", description = "그룹웨어 관련 API")
public class InfoController {

    private final InfoService infoService;

    @Operation(summary = "로그인 사용자 정보 가져오기", description = "세션에서 현재 로그인 된 사용자 정보(사번, 이름)을 가져옵니다.")
    @GetMapping(value = "/")
    public InfoResponseDTO getUserInfo() {
        return infoService.getUserInfo();
    }
}
