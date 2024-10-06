package kr.or.kmi.mis.api.seal.service;

import kr.or.kmi.mis.api.apply.model.request.ApplyRequestDTO;
import kr.or.kmi.mis.api.seal.model.response.*;
import kr.or.kmi.mis.cmm.model.request.PostSearchRequestDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface SealListService {

    /* 인장관리대장 */
    List<ManagementListResponseDTO> getSealManagementList(String searchType, String keyword, String instCd);

    /* 인장반출대장 */
    List<ExportListResponseDTO> getSealExportList(String searchType, String keyword, String instCd);

    /* 인장등록대장(센터별) */
    List<RegistrationListResponseDTO> getSealRegistrationList(String instCd);

    /* 인장등록대장(전국) */
    List<TotalRegistrationListResponseDTO> getTotalSealRegistrationList();

    /* 인장 전체신청내역 */
    List<SealMasterResponseDTO> getSealApply(LocalDateTime startDate, LocalDateTime endDate, String searchType, String keyword, String instCd);
    Page<SealMasterResponseDTO> getSealApply2(ApplyRequestDTO applyRequestDTO, PostSearchRequestDTO postSearchRequestDTO, Pageable pageable);

    /* 인장 승인대기내역 */
    List<SealPendingResponseDTO> getSealPendingList(LocalDateTime startDate, LocalDateTime endDate, String instCd);
    Page<SealPendingResponseDTO> getSealPendingList2(ApplyRequestDTO applyRequestDTO, PostSearchRequestDTO postSearchRequestDTO, Pageable page);

    /* 인장 나의 전체신청내역 */
    List<SealMyResponseDTO> getMySealApply(LocalDateTime startDate, LocalDateTime endDate, String userId);
    List<SealMyResponseDTO> getMySealApply(ApplyRequestDTO applyRequestDTO, PostSearchRequestDTO postSearchRequestDTO);
    Page<SealMyResponseDTO> getMySealApply2(ApplyRequestDTO applyRequestDTO, PostSearchRequestDTO postSearchRequestDTO, Pageable pageable);

    /* 인장 나의 승인대기내역 */
    List<SealPendingResponseDTO> getMySealPendingList(String userId);
}
