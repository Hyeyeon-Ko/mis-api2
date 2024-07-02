package kr.or.kmi.mis.api.sample.service.impl;

import kr.or.kmi.mis.api.sample.model.request.SampleRequestDTO;
import kr.or.kmi.mis.api.sample.model.response.SampleResponseDTO;
import kr.or.kmi.mis.api.sample.service.SampleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * packageName    : kr.or.kmi.mis.api.sample.service.impl
 * fileName       : SampleServiceImpl
 * author         : KMI_DI
 * date           : 2024-07-02
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2024-07-02        KMI_DI       the first create
 */
@Service
@RequiredArgsConstructor
public class SampleServiceImpl implements SampleService {
    @Override
    public SampleResponseDTO getSample(SampleRequestDTO requestDTO) {
        return SampleResponseDTO.builder()
                .id(requestDTO.getId())
                .name("TEST")
                .build();
    }
}
