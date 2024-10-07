package kr.or.kmi.mis.api.doc.repository;

import kr.or.kmi.mis.api.doc.model.request.DocRequestDTO;
import kr.or.kmi.mis.api.doc.model.response.DocResponseDTO;
import kr.or.kmi.mis.cmm.model.request.PostSearchRequestDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * packageName    : kr.or.kmi.mis.api.doc.repository
 * fileName       : DocReceiveQueryRepository
 * author         : KMI_DI
 * date           : 2024-10-07
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2024-10-07        KMI_DI       the first create
 */
public interface DocQueryRepository {
    Page<DocResponseDTO> getDocList(DocRequestDTO docRequestDTO, PostSearchRequestDTO postSearchRequestDTO, Pageable page);
}
