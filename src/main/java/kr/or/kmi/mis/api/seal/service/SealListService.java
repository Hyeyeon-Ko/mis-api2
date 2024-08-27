package kr.or.kmi.mis.api.seal.service;

import kr.or.kmi.mis.api.seal.model.response.ExportListResponseDTO;
import kr.or.kmi.mis.api.seal.model.response.ManagementListResponseDTO;
import kr.or.kmi.mis.api.seal.model.response.RegistrationListResponseDTO;

import java.time.LocalDate;
import java.util.List;

public interface SealListService {

    /* 인장관리대장 */
    List<ManagementListResponseDTO> getSealManagementList(LocalDate startDate, LocalDate endDate);

    /* 인장반출대장 */
    List<ExportListResponseDTO> getSealExportList(LocalDate startDate, LocalDate endDate);

    /* 인장등록대장 */
    List<RegistrationListResponseDTO> getSealRegistrationList(LocalDate startDate, LocalDate endDate);
}
