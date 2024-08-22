package kr.or.kmi.mis.api.rental.model.response;

import kr.or.kmi.mis.api.rental.model.entity.RentalDetail;
import lombok.Builder;
import lombok.Getter;

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
