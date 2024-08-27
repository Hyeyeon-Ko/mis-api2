package kr.or.kmi.mis.api.seal.service.impl;

import kr.or.kmi.mis.api.seal.model.response.ExportListResponseDTO;
import kr.or.kmi.mis.api.seal.model.response.ManagementListResponseDTO;
import kr.or.kmi.mis.api.seal.model.response.RegistrationListResponseDTO;
import kr.or.kmi.mis.api.seal.service.SealListService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SealListServiceImpl implements SealListService {

    @Override
    public List<ManagementListResponseDTO> getSealManagementList(LocalDate startDate, LocalDate endDate) {
        return List.of();
    }

    @Override
    public List<ExportListResponseDTO> getSealExportList(LocalDate startDate, LocalDate endDate) {
        return List.of();
    }

    @Override
    public List<RegistrationListResponseDTO> getSealRegistrationList(LocalDate startDate, LocalDate endDate) {
        return List.of();
    }
}
