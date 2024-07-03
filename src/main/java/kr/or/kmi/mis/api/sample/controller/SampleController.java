package kr.or.kmi.mis.api.sample.controller;

import kr.or.kmi.mis.api.sample.model.request.SampleRequestDTO;
import kr.or.kmi.mis.api.sample.model.response.SampleResponseDTO;
import kr.or.kmi.mis.api.sample.service.SampleService;
import kr.or.kmi.mis.cmm.response.ApiResponse;
import kr.or.kmi.mis.cmm.response.ResponseWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * packageName    : kr.or.kmi.mis.api.sample.controller
 * fileName       : SampleController
 * author         : KMI_DI
 * date           : 2024-07-02
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2024-07-02        KMI_DI       the first create
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/sample")
public class SampleController {
    private final SampleService sampleService;

    @PostMapping("/sample")
    public ApiResponse<SampleResponseDTO> sample(@RequestBody SampleRequestDTO requestDTO) {
        return ResponseWrapper.success(sampleService.getSample(requestDTO));
    }
}
