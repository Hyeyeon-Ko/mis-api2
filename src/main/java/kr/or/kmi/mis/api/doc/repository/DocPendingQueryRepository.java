package kr.or.kmi.mis.api.doc.repository;

import kr.or.kmi.mis.api.apply.model.request.ApplyRequestDTO;
import kr.or.kmi.mis.api.doc.model.response.DocPendingResponseDTO;
import kr.or.kmi.mis.cmm.model.request.PostSearchRequestDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface DocPendingQueryRepository {
    Page<DocPendingResponseDTO> getDocPending2(ApplyRequestDTO applyRequestDTO, PostSearchRequestDTO postSearchRequestDTO, Pageable page);
    Long getDocPendingCount(ApplyRequestDTO applyRequestDTO, PostSearchRequestDTO postSearchRequestDTO);
}
