package kr.or.kmi.mis.api.doc.repository;

import kr.or.kmi.mis.api.apply.model.request.ApplyRequestDTO;
import kr.or.kmi.mis.api.doc.model.response.DocMasterResponseDTO;
import kr.or.kmi.mis.api.doc.model.response.DocMyResponseDTO;
import kr.or.kmi.mis.cmm.model.request.PostSearchRequestDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * packageName    : kr.or.kmi.mis.api.doc.repository
 * fileName       : DocApplyQueryRepository
 * author         : KMI_DI
 * date           : 2024-10-02
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2024-10-02        KMI_DI       the first create
 */
public interface DocApplyQueryRepository {
    Page<DocMasterResponseDTO> getDocApply2(ApplyRequestDTO applyRequestDTO, PostSearchRequestDTO postSearchRequestDTO, Pageable page);
    Page<DocMyResponseDTO> getMyDocApply2(ApplyRequestDTO applyRequestDTO, PostSearchRequestDTO postSearchRequestDTO, Pageable page);
    List<DocMyResponseDTO> getMyDocMasterList2(ApplyRequestDTO applyRequestDTO, PostSearchRequestDTO postSearchRequestDTO);
}
