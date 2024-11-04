package kr.or.kmi.mis.api.toner.repository;

import kr.or.kmi.mis.api.apply.model.request.ApplyRequestDTO;
import kr.or.kmi.mis.api.toner.model.response.TonerMasterResponseDTO;
import kr.or.kmi.mis.api.toner.model.response.TonerMyResponseDTO;
import kr.or.kmi.mis.cmm.model.request.PostSearchRequestDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface TonerApplyQueryRepository {

    List<TonerMyResponseDTO> getMyTonerApply(ApplyRequestDTO applyRequestDTO, PostSearchRequestDTO postSearchRequestDTO);
    Page<TonerMyResponseDTO> getMyTonerApply2(ApplyRequestDTO applyRequestDTO, PostSearchRequestDTO postSearchRequestDTO, Pageable pageable);

    Page<TonerMasterResponseDTO> getTonerApply2(ApplyRequestDTO applyRequestDTO, PostSearchRequestDTO postSearchRequestDTO, Pageable pageable);
}
