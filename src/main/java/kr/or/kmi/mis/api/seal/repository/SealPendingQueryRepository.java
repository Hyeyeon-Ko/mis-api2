package kr.or.kmi.mis.api.seal.repository;

import kr.or.kmi.mis.api.apply.model.request.ApplyRequestDTO;
import kr.or.kmi.mis.api.seal.model.response.SealPendingResponseDTO;
import kr.or.kmi.mis.cmm.model.request.PostSearchRequestDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface SealPendingQueryRepository {
    Page<SealPendingResponseDTO> getSealPending2(ApplyRequestDTO applyRequestDTO, PostSearchRequestDTO postSearchRequestDTO, Pageable page);
    Long getSealPendingCount(ApplyRequestDTO applyRequestDTO, PostSearchRequestDTO postSearchRequestDTO);
}
