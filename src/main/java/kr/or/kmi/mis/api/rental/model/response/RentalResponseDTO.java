package kr.or.kmi.mis.api.rental.model.response;

import kr.or.kmi.mis.api.rental.model.entity.RentalDetail;
import kr.or.kmi.mis.cmm.model.entity.BaseSystemFieldEntity;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Getter
@Builder
public class RentalResponseDTO {

    private Long detailId;
    private String instCd;
    private String category;
    private String companyNm;
    private String contractNum;
    private String modelNm;
    private String installDate;
    private String expiryDate;
    private String rentalFee;
    private String location;
    private String installationSite;
    private String specialNote;
    private String status;
    private String lastUpdtDate;

    public static RentalResponseDTO of(List<RentalDetail> rentalDetails) {
        LocalDateTime lastUpdateDate = rentalDetails.stream()
                .map(BaseSystemFieldEntity::getUpdtDt)
                .filter(Objects::nonNull)
                .max(LocalDateTime::compareTo)
                .orElse(null);

        if (lastUpdateDate == null) {
            lastUpdateDate = rentalDetails.stream()
                    .map(BaseSystemFieldEntity::getRgstDt)
                    .filter(Objects::nonNull)
                    .max(LocalDateTime::compareTo)
                    .orElse(null);
        }

        RentalDetail firstDetail = rentalDetails.get(0);
        return RentalResponseDTO.builder()
                .detailId(firstDetail.getDetailId())
                .instCd(firstDetail.getInstCd())
                .category(firstDetail.getCategory())
                .companyNm(firstDetail.getCompanyNm())
                .contractNum(firstDetail.getContractNum())
                .modelNm(firstDetail.getModelNm())
                .installDate(firstDetail.getInstallDate())
                .expiryDate(firstDetail.getExpiryDate())
                .rentalFee(firstDetail.getRentalFee())
                .location(firstDetail.getLocation())
                .installationSite(firstDetail.getInstallationSite())
                .specialNote(firstDetail.getSpecialNote())
                .status(firstDetail.getStatus())
                .lastUpdtDate(lastUpdateDate != null ? lastUpdateDate.toLocalDate().toString() : null)
                .build();
    }

    public static RentalResponseDTO of(RentalDetail rentalDetail) {
        return RentalResponseDTO.builder()
                .detailId(rentalDetail.getDetailId())
                .instCd(rentalDetail.getInstCd())
                .category(rentalDetail.getCategory())
                .companyNm(rentalDetail.getCompanyNm())
                .contractNum(rentalDetail.getContractNum())
                .modelNm(rentalDetail.getModelNm())
                .installDate(rentalDetail.getInstallDate())
                .expiryDate(rentalDetail.getExpiryDate())
                .rentalFee(rentalDetail.getRentalFee())
                .location(rentalDetail.getLocation())
                .installationSite(rentalDetail.getInstallationSite())
                .specialNote(rentalDetail.getSpecialNote())
                .status(rentalDetail.getStatus())
                .build();
    }

}
