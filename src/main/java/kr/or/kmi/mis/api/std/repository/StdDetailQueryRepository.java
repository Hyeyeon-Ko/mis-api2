package kr.or.kmi.mis.api.std.repository;

import kr.or.kmi.mis.api.std.model.response.StdDetailResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * packageName    : kr.or.kmi.mis.api.std.repository
 * fileName       : StdDetailQueryRepository
 * author         : KMI_DI
 * date           : 2024-10-01
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2024-10-01        KMI_DI       the first create
 */
public interface StdDetailQueryRepository {
    Page<StdDetailResponseDTO> getInfo2(String groupCd, Pageable page);
    String findDetailCd(String teamCd, String instCd);
}
