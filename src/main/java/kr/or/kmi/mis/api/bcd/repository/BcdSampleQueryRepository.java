package kr.or.kmi.mis.api.bcd.repository;

import kr.or.kmi.mis.api.bcd.model.response.BcdSampleResponseDTO;

/**
 * packageName    : kr.or.kmi.mis.api.bcd.repository
 * fileName       : BcdSampleQueryRepository
 * author         : KMI_DI
 * date           : 2024-07-17
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2024-07-17        KMI_DI       the first create
 */
public interface BcdSampleQueryRepository {
    BcdSampleResponseDTO getBcdSampleNm(String groupCd, String detailCd);
}
