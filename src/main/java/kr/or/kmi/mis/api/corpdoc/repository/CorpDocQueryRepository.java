package kr.or.kmi.mis.api.corpdoc.repository;

import kr.or.kmi.mis.api.apply.model.request.ApplyRequestDTO;
import kr.or.kmi.mis.api.corpdoc.model.response.CorpDocMasterResponseDTO;
import kr.or.kmi.mis.cmm.model.request.PostSearchRequestDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * packageName    : kr.or.kmi.mis.api.corpdoc.repository
 * fileName       : CorpDocQueryRepository
 * author         : KMI_DI
 * date           : 2024-10-04
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2024-10-04        KMI_DI       the first create
 */
public interface CorpDocQueryRepository {
    Page<CorpDocMasterResponseDTO> getCorpDocApply2(ApplyRequestDTO applyRequestDTO, PostSearchRequestDTO postSearchRequestDTO, Pageable page);
}
