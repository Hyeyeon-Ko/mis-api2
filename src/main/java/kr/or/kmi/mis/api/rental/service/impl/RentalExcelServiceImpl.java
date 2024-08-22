package kr.or.kmi.mis.api.rental.service.impl;

import jakarta.servlet.http.HttpServletResponse;
import kr.or.kmi.mis.api.rental.model.entity.RentalDetail;
import kr.or.kmi.mis.api.rental.model.response.RentalExcelResponseDTO;
import kr.or.kmi.mis.api.rental.repository.RentalDetailRepository;
import kr.or.kmi.mis.api.rental.service.RentalExcelService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class RentalExcelServiceImpl implements RentalExcelService {

    private final RentalDetailRepository rentalDetailRepository;

    @Override
    public void downloadExcel(HttpServletResponse response, List<Long> detailIds) {

    }

    @Override
    public byte[] generateExcel(List<Long> detailIds) throws IOException {
        return new byte[0];
    }

    @Override
    public void saveRentalDetails(List<RentalExcelResponseDTO> details) {

        List<RentalDetail> entities = details.stream().map(dto -> {
            if (rentalDetailRepository.existsByContractNum(dto.getContractNum())) {
                throw new IllegalArgumentException("계약번호가 중복됩니다: " + dto.getContractNum());
            }

            return RentalDetail.builder()
                    .category(dto.getCategory())
                    .companyNm(dto.getCompanyNm())
                    .contractNum(dto.getContractNum())
                    .modelNm(dto.getModelNm())
                    .installDate(dto.getInstallDate())
                    .expiryDate(dto.getExpiryDate())
                    .rentalFee(dto.getRentalFee())
                    .location(dto.getLocation())
                    .installationSite(dto.getInstallationSite())
                    .specialNote(dto.getSpecialNote())
                    .instCd(dto.getInstCd())
                    .build();
        }).collect(Collectors.toList());

        rentalDetailRepository.saveAll(entities);
    }

    @Override
    @Transactional
    public void updateRentalDetails(List<RentalExcelResponseDTO> details) {
        details.forEach(dto -> {
            RentalDetail existingDetail = rentalDetailRepository.findByContractNum(dto.getContractNum())
                    .orElseThrow(() -> new IllegalArgumentException("Not Found"));

            existingDetail.updateExcelData(dto);

            rentalDetailRepository.save(existingDetail);
        });
    }
}
