package kr.or.kmi.mis.api.sample.service;

import kr.or.kmi.mis.api.sample.model.request.SampleRequestDTO;
import kr.or.kmi.mis.api.sample.model.response.SampleResponseDTO;

/**
 * packageName    : kr.or.kmi.mis.api.sample.service
 * fileName       : SampleService
 * author         : KMI_DI
 * date           : 2024-07-02
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2024-07-02        KMI_DI       the first create
 */

public interface SampleService {
    SampleResponseDTO getSample(SampleRequestDTO requestDTO);
}
