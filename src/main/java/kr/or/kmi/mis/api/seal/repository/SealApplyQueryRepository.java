package kr.or.kmi.mis.api.seal.repository;

import kr.or.kmi.mis.api.apply.model.request.ApplyRequestDTO;
import kr.or.kmi.mis.api.seal.model.response.ManagementListResponseDTO;
import kr.or.kmi.mis.api.seal.model.response.SealMasterResponseDTO;
import kr.or.kmi.mis.cmm.model.request.PostSearchRequestDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * packageName    : kr.or.kmi.mis.api.seal.repository
 * fileName       : SealApplyQueryRepository
 * author         : KMI_DI
 * date           : 2024-10-04
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2024-10-04        KMI_DI       the first create
 */
public interface SealApplyQueryRepository {
    Page<SealMasterResponseDTO> getSealApply2(ApplyRequestDTO applyRequestDTO, PostSearchRequestDTO postSearchRequestDTO, Pageable page);
}
