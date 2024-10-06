package kr.or.kmi.mis.api.seal.repository;

import kr.or.kmi.mis.api.apply.model.request.ApplyRequestDTO;
import kr.or.kmi.mis.api.seal.model.response.ExportListResponseDTO;
import kr.or.kmi.mis.api.seal.model.response.ManagementListResponseDTO;
import kr.or.kmi.mis.api.seal.model.response.RegistrationListResponseDTO;
import kr.or.kmi.mis.api.seal.model.response.TotalRegistrationListResponseDTO;
import kr.or.kmi.mis.cmm.model.request.PostSearchRequestDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface SealListQueryRepository {
    Page<ManagementListResponseDTO> getSealManagementList(ApplyRequestDTO applyRequestDTO, PostSearchRequestDTO postSearchRequestDTO, Pageable page);
    Page<ExportListResponseDTO> getSealExportList(ApplyRequestDTO applyRequestDTO, PostSearchRequestDTO postSearchRequestDTO, Pageable pageable);
    Page<RegistrationListResponseDTO> getRegistrationList(ApplyRequestDTO applyRequestDTO, Pageable page);
    Page<TotalRegistrationListResponseDTO> getTotalSealRegistrationList(Pageable page);
}
