package kr.or.kmi.mis.api.seal.service;

import kr.or.kmi.mis.api.seal.model.response.*;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.List;

public interface SealListService {

    /* 인장관리대장 */
    List<ManagementListResponseDTO> getSealManagementList(LocalDate startDate, LocalDate endDate, String instCd);

    /* 인장반출대장 */
    List<ExportListResponseDTO> getSealExportList(LocalDate startDate, LocalDate endDate, String instCd);

    /* 인장등록대장(센터별) */
    List<RegistrationListResponseDTO> getSealRegistrationList(String instCd);

    /* 인장등록대장(전국) */
    List<TotalRegistrationListResponseDTO> getTotalSealRegistrationList();

    /* 인장 전체신청내역 */
    List<SealMasterResponseDTO> getSealApplyByDateRangeAndInstCdAndSearch(Timestamp startDate, Timestamp endDate, String searchType, String keyword, String instCd);

    /* 인장 승인대기내역 */
    List<SealPendingResponseDTO> getSealPendingList(Timestamp startDate, Timestamp endDate, String instCd);

    /* 인장 나의 전체신청내역 */
    List<SealMyResponseDTO> getMySealApplyByDateRange(Timestamp startDate, Timestamp endDate,String userId);

    /* 인장 나의 승인대기내역 */
    List<SealPendingResponseDTO> getMySealPendingList(String userId);
}
